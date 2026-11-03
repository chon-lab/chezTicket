package com.chezticket.catalogo.evento.application.port.out;

import com.chezticket.catalogo.evento.application.port.in.ImagemUpload;

public interface ArmazenarImagemPort {

    String armazenar(ImagemUpload imagem);

    void excluir(String url);
}
