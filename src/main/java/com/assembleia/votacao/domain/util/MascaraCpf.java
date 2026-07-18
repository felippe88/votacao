package com.assembleia.votacao.domain.util;

public final class MascaraCpf {

    private MascaraCpf() {
    }

    public static String mascarar(String cpf) {
        if (cpf == null || cpf.length() <= 3) {
            return "***";
        }
        return "*".repeat(cpf.length() - 3) + cpf.substring(cpf.length() - 3);
    }
}
