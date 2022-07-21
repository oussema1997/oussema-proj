package com.sfm.obd.dto;

import java.util.List;

import lombok.Data;

@Data
public class EntityPage<T> {

	private List<T> list;
	private PageUtil pageUtil;

}
