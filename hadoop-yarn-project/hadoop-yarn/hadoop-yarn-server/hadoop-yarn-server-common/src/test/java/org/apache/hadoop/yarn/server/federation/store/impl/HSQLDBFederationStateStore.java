begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * HSQLDB implementation of {@link FederationStateStore}.  */
end_comment

begin_class
DECL|class|HSQLDBFederationStateStore
specifier|public
class|class
name|HSQLDBFederationStateStore
extends|extends
name|SQLFederationStateStore
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HSQLDBFederationStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conn
specifier|private
name|Connection
name|conn
decl_stmt|;
DECL|field|TABLE_APPLICATIONSHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|TABLE_APPLICATIONSHOMESUBCLUSTER
init|=
literal|" CREATE TABLE applicationsHomeSubCluster ("
operator|+
literal|" applicationId varchar(64) NOT NULL,"
operator|+
literal|" homeSubCluster varchar(256) NOT NULL,"
operator|+
literal|" CONSTRAINT pk_applicationId PRIMARY KEY (applicationId))"
decl_stmt|;
DECL|field|TABLE_MEMBERSHIP
specifier|private
specifier|static
specifier|final
name|String
name|TABLE_MEMBERSHIP
init|=
literal|"CREATE TABLE membership ( subClusterId varchar(256) NOT NULL,"
operator|+
literal|" amRMServiceAddress varchar(256) NOT NULL,"
operator|+
literal|" clientRMServiceAddress varchar(256) NOT NULL,"
operator|+
literal|" rmAdminServiceAddress varchar(256) NOT NULL,"
operator|+
literal|" rmWebServiceAddress varchar(256) NOT NULL,"
operator|+
literal|" lastHeartBeat datetime NOT NULL, state varchar(32) NOT NULL,"
operator|+
literal|" lastStartTime bigint NULL, capability varchar(6000) NOT NULL,"
operator|+
literal|" CONSTRAINT pk_subClusterId PRIMARY KEY (subClusterId))"
decl_stmt|;
DECL|field|TABLE_POLICIES
specifier|private
specifier|static
specifier|final
name|String
name|TABLE_POLICIES
init|=
literal|"CREATE TABLE policies ( queue varchar(256) NOT NULL,"
operator|+
literal|" policyType varchar(256) NOT NULL, params varbinary(512),"
operator|+
literal|" CONSTRAINT pk_queue PRIMARY KEY (queue))"
decl_stmt|;
DECL|field|SP_REGISTERSUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_REGISTERSUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_registerSubCluster("
operator|+
literal|" IN subClusterId_IN varchar(256),"
operator|+
literal|" IN amRMServiceAddress_IN varchar(256),"
operator|+
literal|" IN clientRMServiceAddress_IN varchar(256),"
operator|+
literal|" IN rmAdminServiceAddress_IN varchar(256),"
operator|+
literal|" IN rmWebServiceAddress_IN varchar(256),"
operator|+
literal|" IN state_IN varchar(256),"
operator|+
literal|" IN lastStartTime_IN bigint, IN capability_IN varchar(6000),"
operator|+
literal|" OUT rowCount_OUT int)MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" DELETE FROM membership WHERE (subClusterId = subClusterId_IN);"
operator|+
literal|" INSERT INTO membership ( subClusterId,"
operator|+
literal|" amRMServiceAddress, clientRMServiceAddress,"
operator|+
literal|" rmAdminServiceAddress, rmWebServiceAddress,"
operator|+
literal|" lastHeartBeat, state, lastStartTime,"
operator|+
literal|" capability) VALUES ( subClusterId_IN,"
operator|+
literal|" amRMServiceAddress_IN, clientRMServiceAddress_IN,"
operator|+
literal|" rmAdminServiceAddress_IN, rmWebServiceAddress_IN,"
operator|+
literal|" NOW() AT TIME ZONE INTERVAL '0:00' HOUR TO MINUTE,"
operator|+
literal|" state_IN, lastStartTime_IN, capability_IN);"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_DEREGISTERSUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_DEREGISTERSUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_deregisterSubCluster("
operator|+
literal|" IN subClusterId_IN varchar(256),"
operator|+
literal|" IN state_IN varchar(64), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" UPDATE membership SET state = state_IN WHERE ("
operator|+
literal|" subClusterId = subClusterId_IN AND state != state_IN);"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_SUBCLUSTERHEARTBEAT
specifier|private
specifier|static
specifier|final
name|String
name|SP_SUBCLUSTERHEARTBEAT
init|=
literal|"CREATE PROCEDURE sp_subClusterHeartbeat("
operator|+
literal|" IN subClusterId_IN varchar(256), IN state_IN varchar(64),"
operator|+
literal|" IN capability_IN varchar(6000), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC UPDATE membership"
operator|+
literal|" SET capability = capability_IN, state = state_IN,"
operator|+
literal|" lastHeartBeat = NOW() AT TIME ZONE INTERVAL '0:00'"
operator|+
literal|" HOUR TO MINUTE WHERE subClusterId = subClusterId_IN;"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_GETSUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETSUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_getSubCluster( IN subClusterId_IN varchar(256),"
operator|+
literal|" OUT amRMServiceAddress_OUT varchar(256),"
operator|+
literal|" OUT clientRMServiceAddress_OUT varchar(256),"
operator|+
literal|" OUT rmAdminServiceAddress_OUT varchar(256),"
operator|+
literal|" OUT rmWebServiceAddress_OUT varchar(256),"
operator|+
literal|" OUT lastHeartBeat_OUT datetime, OUT state_OUT varchar(64),"
operator|+
literal|" OUT lastStartTime_OUT bigint,"
operator|+
literal|" OUT capability_OUT varchar(6000))"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC SELECT amRMServiceAddress,"
operator|+
literal|" clientRMServiceAddress,"
operator|+
literal|" rmAdminServiceAddress, rmWebServiceAddress,"
operator|+
literal|" lastHeartBeat, state, lastStartTime, capability"
operator|+
literal|" INTO amRMServiceAddress_OUT, clientRMServiceAddress_OUT,"
operator|+
literal|" rmAdminServiceAddress_OUT,"
operator|+
literal|" rmWebServiceAddress_OUT, lastHeartBeat_OUT,"
operator|+
literal|" state_OUT, lastStartTime_OUT, capability_OUT"
operator|+
literal|" FROM membership WHERE subClusterId = subClusterId_IN; END"
decl_stmt|;
DECL|field|SP_GETSUBCLUSTERS
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETSUBCLUSTERS
init|=
literal|"CREATE PROCEDURE sp_getSubClusters()"
operator|+
literal|" MODIFIES SQL DATA DYNAMIC RESULT SETS 1 BEGIN ATOMIC"
operator|+
literal|" DECLARE result CURSOR FOR"
operator|+
literal|" SELECT subClusterId, amRMServiceAddress, clientRMServiceAddress,"
operator|+
literal|" rmAdminServiceAddress, rmWebServiceAddress, lastHeartBeat,"
operator|+
literal|" state, lastStartTime, capability"
operator|+
literal|" FROM membership; OPEN result; END"
decl_stmt|;
DECL|field|SP_ADDAPPLICATIONHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_ADDAPPLICATIONHOMESUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_addApplicationHomeSubCluster("
operator|+
literal|" IN applicationId_IN varchar(64),"
operator|+
literal|" IN homeSubCluster_IN varchar(256),"
operator|+
literal|" OUT storedHomeSubCluster_OUT varchar(256), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" INSERT INTO applicationsHomeSubCluster "
operator|+
literal|" (applicationId,homeSubCluster) "
operator|+
literal|" (SELECT applicationId_IN, homeSubCluster_IN"
operator|+
literal|" FROM applicationsHomeSubCluster"
operator|+
literal|" WHERE applicationId = applicationId_IN"
operator|+
literal|" HAVING COUNT(*) = 0 );"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT;"
operator|+
literal|" SELECT homeSubCluster INTO storedHomeSubCluster_OUT"
operator|+
literal|" FROM applicationsHomeSubCluster"
operator|+
literal|" WHERE applicationId = applicationID_IN; END"
decl_stmt|;
DECL|field|SP_UPDATEAPPLICATIONHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_UPDATEAPPLICATIONHOMESUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_updateApplicationHomeSubCluster("
operator|+
literal|" IN applicationId_IN varchar(64),"
operator|+
literal|" IN homeSubCluster_IN varchar(256), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" UPDATE applicationsHomeSubCluster"
operator|+
literal|" SET homeSubCluster = homeSubCluster_IN"
operator|+
literal|" WHERE applicationId = applicationId_IN;"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_GETAPPLICATIONHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETAPPLICATIONHOMESUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_getApplicationHomeSubCluster("
operator|+
literal|" IN applicationId_IN varchar(64),"
operator|+
literal|" OUT homeSubCluster_OUT varchar(256))"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" SELECT homeSubCluster INTO homeSubCluster_OUT"
operator|+
literal|" FROM applicationsHomeSubCluster"
operator|+
literal|" WHERE applicationId = applicationID_IN; END"
decl_stmt|;
DECL|field|SP_GETAPPLICATIONSHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETAPPLICATIONSHOMESUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_getApplicationsHomeSubCluster()"
operator|+
literal|" MODIFIES SQL DATA DYNAMIC RESULT SETS 1 BEGIN ATOMIC"
operator|+
literal|" DECLARE result CURSOR FOR"
operator|+
literal|" SELECT applicationId, homeSubCluster"
operator|+
literal|" FROM applicationsHomeSubCluster; OPEN result; END"
decl_stmt|;
DECL|field|SP_DELETEAPPLICATIONHOMESUBCLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|SP_DELETEAPPLICATIONHOMESUBCLUSTER
init|=
literal|"CREATE PROCEDURE sp_deleteApplicationHomeSubCluster("
operator|+
literal|" IN applicationId_IN varchar(64), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" DELETE FROM applicationsHomeSubCluster"
operator|+
literal|" WHERE applicationId = applicationId_IN;"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_SETPOLICYCONFIGURATION
specifier|private
specifier|static
specifier|final
name|String
name|SP_SETPOLICYCONFIGURATION
init|=
literal|"CREATE PROCEDURE sp_setPolicyConfiguration("
operator|+
literal|" IN queue_IN varchar(256), IN policyType_IN varchar(256),"
operator|+
literal|" IN params_IN varbinary(512), OUT rowCount_OUT int)"
operator|+
literal|" MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" DELETE FROM policies WHERE queue = queue_IN;"
operator|+
literal|" INSERT INTO policies (queue, policyType, params)"
operator|+
literal|" VALUES (queue_IN, policyType_IN, params_IN);"
operator|+
literal|" GET DIAGNOSTICS rowCount_OUT = ROW_COUNT; END"
decl_stmt|;
DECL|field|SP_GETPOLICYCONFIGURATION
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETPOLICYCONFIGURATION
init|=
literal|"CREATE PROCEDURE sp_getPolicyConfiguration("
operator|+
literal|" IN queue_IN varchar(256), OUT policyType_OUT varchar(256),"
operator|+
literal|" OUT params_OUT varbinary(512)) MODIFIES SQL DATA BEGIN ATOMIC"
operator|+
literal|" SELECT policyType, params INTO policyType_OUT, params_OUT"
operator|+
literal|" FROM policies WHERE queue = queue_IN; END"
decl_stmt|;
DECL|field|SP_GETPOLICIESCONFIGURATIONS
specifier|private
specifier|static
specifier|final
name|String
name|SP_GETPOLICIESCONFIGURATIONS
init|=
literal|"CREATE PROCEDURE sp_getPoliciesConfigurations()"
operator|+
literal|" MODIFIES SQL DATA DYNAMIC RESULT SETS 1 BEGIN ATOMIC"
operator|+
literal|" DECLARE result CURSOR FOR"
operator|+
literal|" SELECT * FROM policies; OPEN result; END"
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERROR: failed to init HSQLDB "
operator|+
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|conn
operator|=
name|getConnection
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Database Init: Start"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|TABLE_APPLICATIONSHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|TABLE_MEMBERSHIP
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|TABLE_POLICIES
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_REGISTERSUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_DEREGISTERSUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_SUBCLUSTERHEARTBEAT
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETSUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETSUBCLUSTERS
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_ADDAPPLICATIONHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_UPDATEAPPLICATIONHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETAPPLICATIONHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETAPPLICATIONSHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_DELETEAPPLICATIONHOMESUBCLUSTER
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_SETPOLICYCONFIGURATION
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETPOLICYCONFIGURATION
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|conn
operator|.
name|prepareStatement
argument_list|(
name|SP_GETPOLICIESCONFIGURATIONS
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Database Init: Complete"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERROR: failed to inizialize HSQLDB "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeConnection ()
specifier|public
name|void
name|closeConnection
parameter_list|()
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERROR: failed to close connection to HSQLDB DB "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

