package com.seafood.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seafood.entity.Admin;
import com.seafood.entity.NodeEnterprise;
import com.seafood.mapper.AdminMapper;
import com.seafood.mapper.NodeEnterpriseMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminMapper adminMapper;
    private final NodeEnterpriseMapper nodeEnterpriseMapper;

    public CustomUserDetailsService(AdminMapper adminMapper, NodeEnterpriseMapper nodeEnterpriseMapper) {
        this.adminMapper = adminMapper;
        this.nodeEnterpriseMapper = nodeEnterpriseMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String loginCode) throws UsernameNotFoundException {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getLoginCode, loginCode));
        if (admin != null) {
            return new SecurityUser(admin.getId(), admin.getLoginCode(), admin.getPassword(), "ROLE_ADMIN", null);
        }
        NodeEnterprise ent = nodeEnterpriseMapper.selectOne(
                new LambdaQueryWrapper<NodeEnterprise>().eq(NodeEnterprise::getLoginCode, loginCode));
        if (ent != null) {
            return new SecurityUser(ent.getId(), ent.getLoginCode(), ent.getPassword(), "ROLE_NODE", ent.getType());
        }
        throw new UsernameNotFoundException("账号不存在：" + loginCode);
    }
}
