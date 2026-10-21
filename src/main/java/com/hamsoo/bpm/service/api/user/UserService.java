package com.hamsoo.bpm.service.api.user;

import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.signup.RegisterRequest;
import com.hamsoo.bpm.model.dto.signup.SignUpRequest;
import com.hamsoo.bpm.model.dto.signup.VerifyUserMailDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.repository.jpa.user.UserRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, Long, UserRepository> {
    User findByIdOrElseThrow(Long id);

    User findByUsernameOrElseThrow(String username);

    User findByUserIdAndCurrentOrganOrElseThrow(Long id);


    void deleteUser(Long id);

    void sendVerificationEmail(User user, String code) throws UnsupportedEncodingException, MessagingException;

    Optional<User> findByNationalCode(String nationalCode);


    UserDto mapToDtoByRoleInfoAndOrgans(User user, Account account);

    UserDto mapToDto(Account account);

    SearchDto findAll(String Search, Boolean filterOrgan, Pageable pageable);

    UserDto mapToDto(User user);

    User getUserObj(SignUpRequest signUpRequest);

    User getUserObj(RegisterRequest signUpRequest);

    User findByUserIdOrElseThrow(Long id);

    Optional<UserDto> getUser(Principal principal);

    Optional<User> findOrganOwner(Long organId);


    Long updateProfileImage(MultipartFile file, Long id);

    FilesDto getProfileImage(Long id);

    User updateHamsooAccountByUsername(String username, UserDto accountDto, MultipartFile image);

    User getHamsooAccountByUsername(String username);

    boolean verifyUserMail(VerifyUserMailDto verifyUserMailDto);

    Optional<User> findByAccountUsername(String username);


    SearchDto listWithoutMemberRole(String Search, Boolean filterOrgan, Pageable pageable, Long curModelVersion);

    SearchDto listWithoutRole(String Search, Boolean filterOrgan, Pageable pageable);

    List<User> findAllPostMember(Long postId);

    List<User> findAllJobMember(Long jobId);

    List<User> findAllUnitMember(Long unitId);

}
