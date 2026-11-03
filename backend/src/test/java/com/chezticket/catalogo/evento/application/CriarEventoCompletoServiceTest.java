package com.chezticket.catalogo.evento.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chezticket.catalogo.evento.application.port.in.CriarEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.GerenciarCicloDeVidaDoEventoUseCase;
import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;
import com.chezticket.catalogo.evento.application.port.in.NovoEvento;
import com.chezticket.catalogo.evento.application.port.in.NovoEventoCompleto;
import com.chezticket.catalogo.evento.application.port.out.ArmazenarImagemPort;
import com.chezticket.catalogo.evento.domain.ClassificacaoEtaria;
import com.chezticket.catalogo.evento.domain.Evento;
import com.chezticket.catalogo.evento.domain.PoliticaReembolso;
import com.chezticket.catalogo.ingresso.application.port.in.CadastrarTipoIngressoUseCase;
import com.chezticket.catalogo.ingresso.application.port.in.NovoTipoIngresso;
import com.chezticket.catalogo.ingresso.domain.TipoIngresso;
import com.chezticket.catalogo.sessao.application.port.in.AgendarSessaoUseCase;
import com.chezticket.catalogo.sessao.application.port.in.NovaSessao;
import com.chezticket.catalogo.sessao.domain.Sessao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CriarEventoCompletoServiceTest {

    private final CriarEventoUseCase criarEvento = mock(CriarEventoUseCase.class);
    private final AgendarSessaoUseCase agendarSessao = mock(AgendarSessaoUseCase.class);
    private final CadastrarTipoIngressoUseCase cadastrarIngresso = mock(CadastrarTipoIngressoUseCase.class);
    private final GerenciarCicloDeVidaDoEventoUseCase cicloDeVida =
            mock(GerenciarCicloDeVidaDoEventoUseCase.class);
    private final ArmazenarImagemPort armazenamento = mock(ArmazenarImagemPort.class);
    private final CriarEventoCompletoService service = new CriarEventoCompletoService(
            criarEvento, agendarSessao, cadastrarIngresso, cicloDeVida, armazenamento);

    private Evento evento;
    private Sessao sessao;
    private TipoIngresso ingresso;
    private ImagemUpload capa;
    private ImagemUpload divulgacao;
    private NovoEventoCompleto comando;

    @BeforeEach
    void setUp() {
        evento = mock(Evento.class);
        sessao = mock(Sessao.class);
        ingresso = mock(TipoIngresso.class);
        capa = new ImagemUpload(new byte[] {1}, "image/png");
        divulgacao = new ImagemUpload(new byte[] {2}, "image/jpeg");
        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        comando = new NovoEventoCompleto(
                2L,
                9L,
                "Encontro de IA",
                "Evento de tecnologia.",
                ClassificacaoEtaria.LIVRE,
                PoliticaReembolso.PADRAO,
                "Av. Exemplo, 100 — Rio de Janeiro, RJ",
                inicio,
                inicio.plusHours(3),
                List.of("IA"),
                Map.of("instagram", "https://instagram.com/exemplo"),
                List.of(new NovoEventoCompleto.NovoIngresso(
                        "Inteira",
                        false,
                        new BigDecimal("100.00"),
                        new BigDecimal("10.00"),
                        200,
                        5,
                        LocalDate.now(),
                        LocalDate.now().plusDays(10))),
                capa,
                divulgacao,
                true);
        when(armazenamento.armazenar(any(ImagemUpload.class)))
                .thenReturn("/api/uploads/capa.png", "/api/uploads/divulgacao.jpg");
        when(criarEvento.criar(any(NovoEvento.class))).thenReturn(evento);
        when(evento.id()).thenReturn(11L);
        when(agendarSessao.agendar(any(NovaSessao.class))).thenReturn(sessao);
        when(sessao.id()).thenReturn(22L);
        when(cadastrarIngresso.cadastrar(any(NovoTipoIngresso.class))).thenReturn(ingresso);
        when(cicloDeVida.publicar(11L)).thenReturn(evento);
    }

    @Test
    void criaEventoSessaoIngressosEPublicaNaOrdemDoFluxo() {
        var criado = service.criar(comando);
        var sessaoComando = forClass(NovaSessao.class);

        assertThat(criado.evento()).isSameAs(evento);
        assertThat(criado.sessao()).isSameAs(sessao);
        assertThat(criado.ingressos()).containsExactly(ingresso);
        verify(criarEvento).criar(any(NovoEvento.class));
        verify(agendarSessao).agendar(sessaoComando.capture());
        verify(cadastrarIngresso).cadastrar(any(NovoTipoIngresso.class));
        verify(cicloDeVida).publicar(11L);
        assertThat(sessaoComando.getValue().localId()).isNull();
        assertThat(sessaoComando.getValue().endereco()).isEqualTo(comando.endereco());
    }

    @Test
    void removeImagensArmazenadasQuandoOCadastroFalha() {
        when(cadastrarIngresso.cadastrar(any(NovoTipoIngresso.class)))
                .thenThrow(new IllegalStateException("Falha ao cadastrar ingresso"));

        assertThatThrownBy(() -> service.criar(comando))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Falha ao cadastrar ingresso");

        verify(armazenamento).excluir("/api/uploads/capa.png");
        verify(armazenamento).excluir("/api/uploads/divulgacao.jpg");
    }
}
