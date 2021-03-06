package com.website.service.mapper.system;

import com.website.service.entity.system.SysRole;
import com.website.service.entity.system.SysRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysRoleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    long countByExample(SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int deleteByExample(SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int insert(SysRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int insertSelective(SysRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    SysRole selectOneByExample(SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    SysRole selectOneByExampleSelective(@Param("example") SysRoleExample example, @Param("selective") SysRole.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    List<SysRole> selectByExampleSelective(@Param("example") SysRoleExample example, @Param("selective") SysRole.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    List<SysRole> selectByExample(SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    SysRole selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") SysRole.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    SysRole selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    SysRole selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") SysRole record, @Param("example") SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") SysRole record, @Param("example") SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(SysRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SysRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") SysRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SYS_ROLE
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}