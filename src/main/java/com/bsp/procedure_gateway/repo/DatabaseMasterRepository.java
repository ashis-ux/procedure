//package com.bsp.procedure_gateway.repo;
//
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.bsp.procedure_gateway.entity.DatabaseMaster;
//import com.bsp.procedure_gateway.enums.ActiveStatus;
//
//import java.util.Optional;
//
//@Repository
//public interface DatabaseMasterRepository extends JpaRepository<DatabaseMaster, Long> {
//
//    Optional<DatabaseMaster> findByDatabaseIdAndActive(
//            Long databaseId,
//            ActiveStatus active
//    );
//
//}
