package com.hamsoo.bpm.common.config.security.custompremission;

import com.hamsoo.bpm.service.api.identity.*;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private ApplicationContext applicationContext;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            final Authentication authentication,
            final MethodInvocation invocation
    ) {
        final CustomMethodSecurityExpression root = new CustomMethodSecurityExpression(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());
        root.setModelMemberService(applicationContext.getBean(ModelMemberService.class));
        root.setModelDocumentService(applicationContext.getBean(ModelDocumentService.class));
        root.setIdentityIoService(applicationContext.getBean(IdentityIoService.class));
        root.setIoSupCustService(applicationContext.getBean(IoSupCustService.class));
        root.setIdentityKpiService(applicationContext.getBean(IdentityKpiService.class));
        root.setModelElementReferenceService(applicationContext.getBean(ModelElementReferenceService.class));
        root.setModelService(applicationContext.getBean(ModelService.class));
        root.setModelRoleService(applicationContext.getBean(ModelRoleService.class));
        root.setModelCommentService(applicationContext.getBean(ModelCommentService.class));
        root.setModelVersionService(applicationContext.getBean(ModelVersionService.class));
        root.setIdentityComplicationService(applicationContext.getBean(IdentityComplicationService.class));
        root.setIdentityRiskService(applicationContext.getBean(IdentityRiskService.class));
        root.setIdentityResourceService(applicationContext.getBean(IdentityResourceService.class));
        return root;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

}
