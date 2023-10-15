package xyz.dvnlabs.payment.dto;

public record OrdersValidDTO(
        Long id,
        String trxStatus,
        String trxRemark
) {
}
