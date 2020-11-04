package com.airtel.iq.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.airtel.iq.models.CallDetailDocTest;
import com.airtel.iq.models.CallflowConfigurationDO;

public interface CallflowConfigurationRepo extends MongoRepository<CallflowConfigurationDO, String> {

	CallflowConfigurationDO findByCallFlowType(String callFlowType);
    
}
