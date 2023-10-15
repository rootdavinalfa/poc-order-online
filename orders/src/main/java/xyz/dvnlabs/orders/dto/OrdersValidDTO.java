package xyz.dvnlabs.orders.dto;

public record OrdersValidDTO(
        Long id,
        String trxStatus,
        String trxRemark
) {
}
