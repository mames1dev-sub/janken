package oit.is.z3541.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {

  @Select("SELECT id,user1,user2,user1Hand,isActive from matchinfo where isActive = TRUE")
  ArrayList<MatchInfo> selectActiveMatchInfo();

  @Select("SELECT id,user1,user2,user1Hand,isActive from matchinfo where user1 = #{user1} AND user2 = #{user2} AND isActive = TRUE ORDER BY id LIMIT 1")
  MatchInfo selectActiveByUsers(@Param("user1") int user1, @Param("user2") int user2);

  @Insert("INSERT INTO matchinfo (user1,user2,user1Hand,isActive) VALUES (#{user1},#{user2},#{user1Hand},#{isActive});")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertMatchInfo(MatchInfo matchInfo);

  @Update("UPDATE matchinfo SET isActive = #{isActive} WHERE id = #{id}")
  void updateById(MatchInfo matchInfo);
}
