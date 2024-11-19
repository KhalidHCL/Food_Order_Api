package com.hussain.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hussain.dto.FundTransferDto;

import jakarta.validation.Valid;

@FeignClient(value = "BankApiNew", url = "http://localhost:9092/bank")
public interface BankAPIClient {

	@PostMapping("/transfer")
	public String transferFunds(@Valid @RequestBody FundTransferDto fundtransferDto);
}
