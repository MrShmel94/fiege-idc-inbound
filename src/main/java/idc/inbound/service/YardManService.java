package idc.inbound.service;

import idc.inbound.dto.unloading.YardManDTO;

import java.util.List;

public interface YardManService {
    List<YardManDTO> getYardManData();
}
