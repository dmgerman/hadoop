begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * This class contains constants for Recon configuration keys.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ReconServerConfigKeys
specifier|public
specifier|final
class|class
name|ReconServerConfigKeys
block|{
DECL|field|OZONE_RECON_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_ENABLED_KEY
init|=
literal|"ozone.recon.http.enabled"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.recon.http-bind-host"
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.recon.https-bind-host"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_ADDRESS_KEY
init|=
literal|"ozone.recon.http-address"
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.recon.https-address"
decl_stmt|;
DECL|field|OZONE_RECON_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_KEYTAB_FILE
init|=
literal|"ozone.recon.keytab.file"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_RECON_HTTP_BIND_PORT_DEFAULT
init|=
literal|9888
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_RECON_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9889
decl_stmt|;
DECL|field|OZONE_RECON_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
init|=
literal|"ozone.recon.authentication.kerberos.principal"
decl_stmt|;
DECL|field|OZONE_RECON_CONTAINER_DB_CACHE_SIZE_MB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_CONTAINER_DB_CACHE_SIZE_MB
init|=
literal|"ozone.recon.container.db.cache.size.mb"
decl_stmt|;
DECL|field|OZONE_RECON_CONTAINER_DB_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_RECON_CONTAINER_DB_CACHE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|OZONE_RECON_DB_DIR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_DB_DIR
init|=
literal|"ozone.recon.db.dir"
decl_stmt|;
DECL|field|OZONE_RECON_OM_SNAPSHOT_DB_DIR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_OM_SNAPSHOT_DB_DIR
init|=
literal|"ozone.recon.om.db.dir"
decl_stmt|;
DECL|field|RECON_OM_SOCKET_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SOCKET_TIMEOUT
init|=
literal|"recon.om.socket.timeout"
decl_stmt|;
DECL|field|RECON_OM_SOCKET_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SOCKET_TIMEOUT_DEFAULT
init|=
literal|"5s"
decl_stmt|;
DECL|field|RECON_OM_CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_CONNECTION_TIMEOUT
init|=
literal|"recon.om.connection.timeout"
decl_stmt|;
DECL|field|RECON_OM_CONNECTION_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_CONNECTION_TIMEOUT_DEFAULT
init|=
literal|"5s"
decl_stmt|;
DECL|field|RECON_OM_CONNECTION_REQUEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_CONNECTION_REQUEST_TIMEOUT
init|=
literal|"recon.om.connection.request.timeout"
decl_stmt|;
DECL|field|RECON_OM_CONNECTION_REQUEST_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_CONNECTION_REQUEST_TIMEOUT_DEFAULT
init|=
literal|"5s"
decl_stmt|;
DECL|field|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY
init|=
literal|"recon.om.snapshot.task.initial.delay"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY_DEFAULT
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY_DEFAULT
init|=
literal|"1m"
decl_stmt|;
DECL|field|OZONE_RECON_CONTAINER_DB_STORE_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_CONTAINER_DB_STORE_IMPL
init|=
literal|"ozone.recon.container.db.impl"
decl_stmt|;
DECL|field|OZONE_RECON_CONTAINER_DB_STORE_IMPL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_CONTAINER_DB_STORE_IMPL_DEFAULT
init|=
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
decl_stmt|;
DECL|field|RECON_OM_SNAPSHOT_TASK_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SNAPSHOT_TASK_INTERVAL
init|=
literal|"recon.om.snapshot.task.interval.delay"
decl_stmt|;
DECL|field|RECON_OM_SNAPSHOT_TASK_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SNAPSHOT_TASK_INTERVAL_DEFAULT
init|=
literal|"10m"
decl_stmt|;
DECL|field|RECON_OM_SNAPSHOT_TASK_FLUSH_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SNAPSHOT_TASK_FLUSH_PARAM
init|=
literal|"recon.om.snapshot.task.flush.param"
decl_stmt|;
comment|// Persistence properties
DECL|field|OZONE_RECON_SQL_DB_DRIVER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_DB_DRIVER
init|=
literal|"ozone.recon.sql.db.driver"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_DB_JDBC_URL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_DB_JDBC_URL
init|=
literal|"ozone.recon.sql.db.jdbc.url"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_DB_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_DB_USER
init|=
literal|"ozone.recon.sql.db.username"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_DB_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_DB_PASSWORD
init|=
literal|"ozone.recon.sql.db.password"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_AUTO_COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_AUTO_COMMIT
init|=
literal|"ozone.recon.sql.db.auto.commit"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_CONNECTION_TIMEOUT
init|=
literal|"ozone.recon.sql.db.conn.timeout"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_MAX_ACTIVE_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_MAX_ACTIVE_CONNECTIONS
init|=
literal|"ozone.recon.sql.db.conn.max.active"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_MAX_CONNECTION_AGE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_MAX_CONNECTION_AGE
init|=
literal|"ozone.recon.sql.db.conn.max.age"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_MAX_IDLE_CONNECTION_AGE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_MAX_IDLE_CONNECTION_AGE
init|=
literal|"ozone.recon.sql.db.conn.idle.max.age"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_IDLE_CONNECTION_TEST_PERIOD
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_IDLE_CONNECTION_TEST_PERIOD
init|=
literal|"ozone.recon.sql.db.conn.idle.test.period"
decl_stmt|;
DECL|field|OZONE_RECON_SQL_MAX_IDLE_CONNECTION_TEST_STMT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_SQL_MAX_IDLE_CONNECTION_TEST_STMT
init|=
literal|"ozone.recon.sql.db.conn.idle.test"
decl_stmt|;
comment|/**    * Private constructor for utility class.    */
DECL|method|ReconServerConfigKeys ()
specifier|private
name|ReconServerConfigKeys
parameter_list|()
block|{   }
block|}
end_class

end_unit

