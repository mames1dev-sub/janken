package oit.is.z3541.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {

  @Select("SELECT id,user1,user2,user1Hand,user2Hand,isActive from matches")
  ArrayList<Match> selectAllMatches();

  @Select("SELECT COALESCE(MAX(id), 0) FROM matches")
  int selectLastMatchId();

  @Select("SELECT id,user1,user2,user1Hand,user2Hand,isActive FROM matches WHERE user1 = #{user1} AND user2 = #{user2} AND id > #{afterId} AND isActive = TRUE ORDER BY id LIMIT 1")
  Match selectActiveResult(@Param("user1") int user1, @Param("user2") int user2,
      @Param("afterId") int afterId);

  @Insert("INSERT INTO matches (user1,user2,user1Hand,user2Hand,isActive) VALUES (#{user1},#{user2},#{user1Hand},#{user2Hand},#{isActive});")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertMatch(Match match);

  @Update("UPDATE matches SET isActive = #{isActive} WHERE id = #{id}")
  void updateById(Match match);
}
