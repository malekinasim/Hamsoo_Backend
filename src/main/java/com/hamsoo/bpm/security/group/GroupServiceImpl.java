package com.hamsoo.bpm.security.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    GroupRepository groupRepository;
    @Override
    public List<Group> findAllGroupofUsers(String username) {
        return groupRepository.findAllGroupofUsers(username);
    }
}
