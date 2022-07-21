package com.sfm.obd.dao;

import java.util.List;

import com.sfm.obd.model.TimelineBox;
import com.sfm.obd.myrepo.MyRepository;

public interface TimelineBoxDao extends MyRepository<TimelineBox, Long> {
	List<TimelineBox> findAllByBoitierId(long wagonId);
}
