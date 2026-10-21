package com.hamsoo.bpm.security.group;

import java.util.List;

public interface GroupService {
    List<Group> findAllGroupofUsers(String username);
}
