package com.airtel.iq.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.airtel.iq.models.CallDetailDocTest;

public interface CallDetailRepo extends MongoRepository<CallDetailDocTest, String> {

	CallDetailDocTest findByVmSessionid(String vmSessionId);
    
}
