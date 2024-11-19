package com.hussain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferDto {
	@Size(min = 8,max = 8,message = " From Account number should be 8 digit")
	@NotBlank(message = "From account number is required")
    private String fromAccountNumber;
    
	@Size(min = 8,max = 8,message="To aacount number should be 8 didgit" )
    @NotBlank(message = "To account number is required")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    private String comments;
	


}
