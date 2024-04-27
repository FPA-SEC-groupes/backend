package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


}
