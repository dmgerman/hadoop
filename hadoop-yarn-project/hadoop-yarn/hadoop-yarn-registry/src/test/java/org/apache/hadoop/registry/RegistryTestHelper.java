begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|security
operator|.
name|UserGroupInformation
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryUtils
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryTypeUtils
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|AddressTypes
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|Endpoint
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ProtocolTypes
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|yarn
operator|.
name|YarnRegistryAttributes
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
name|registry
operator|.
name|secure
operator|.
name|AbstractSecureRegistryTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|common
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryTypeUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This is a set of static methods to aid testing the registry operations.  * The methods can be imported statically âor the class used as a base  * class for tests.  */
end_comment

begin_class
DECL|class|RegistryTestHelper
specifier|public
class|class
name|RegistryTestHelper
extends|extends
name|Assert
block|{
DECL|field|SC_HADOOP
specifier|public
specifier|static
specifier|final
name|String
name|SC_HADOOP
init|=
literal|"org-apache-hadoop"
decl_stmt|;
DECL|field|USER
specifier|public
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"devteam/"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|API_WEBHDFS
specifier|public
specifier|static
specifier|final
name|String
name|API_WEBHDFS
init|=
literal|"classpath:org.apache.hadoop.namenode.webhdfs"
decl_stmt|;
DECL|field|API_HDFS
specifier|public
specifier|static
specifier|final
name|String
name|API_HDFS
init|=
literal|"classpath:org.apache.hadoop.namenode.dfs"
decl_stmt|;
DECL|field|USERPATH
specifier|public
specifier|static
specifier|final
name|String
name|USERPATH
init|=
name|RegistryConstants
operator|.
name|PATH_USERS
operator|+
name|USER
decl_stmt|;
DECL|field|PARENT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|PARENT_PATH
init|=
name|USERPATH
operator|+
name|SC_HADOOP
operator|+
literal|"/"
decl_stmt|;
DECL|field|ENTRY_PATH
specifier|public
specifier|static
specifier|final
name|String
name|ENTRY_PATH
init|=
name|PARENT_PATH
operator|+
name|NAME
decl_stmt|;
DECL|field|NNIPC
specifier|public
specifier|static
specifier|final
name|String
name|NNIPC
init|=
literal|"uuid:423C2B93-C927-4050-AEC6-6540E6646437"
decl_stmt|;
DECL|field|IPC2
specifier|public
specifier|static
specifier|final
name|String
name|IPC2
init|=
literal|"uuid:0663501D-5AD3-4F7E-9419-52F5D6636FCF"
decl_stmt|;
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
name|RegistryTestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordMarshal
specifier|private
specifier|static
specifier|final
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
name|recordMarshal
init|=
operator|new
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
argument_list|()
decl_stmt|;
DECL|field|HTTP_API
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_API
init|=
literal|"http://"
decl_stmt|;
comment|/**    * Assert the path is valid by ZK rules    * @param path path to check    */
DECL|method|assertValidZKPath (String path)
specifier|public
specifier|static
name|void
name|assertValidZKPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|PathUtils
operator|.
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Path "
operator|+
name|path
operator|+
literal|": "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Assert that a string is not empty (null or "")    * @param message message to raise if the string is empty    * @param check string to check    */
DECL|method|assertNotEmpty (String message, String check)
specifier|public
specifier|static
name|void
name|assertNotEmpty
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|check
parameter_list|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|check
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert that a string is empty (null or "")    * @param check string to check    */
DECL|method|assertNotEmpty (String check)
specifier|public
specifier|static
name|void
name|assertNotEmpty
parameter_list|(
name|String
name|check
parameter_list|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|check
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Empty string"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Log the details of a login context    * @param name name to assert that the user is logged in as    * @param loginContext the login context    */
DECL|method|logLoginDetails (String name, LoginContext loginContext)
specifier|public
specifier|static
name|void
name|logLoginDetails
parameter_list|(
name|String
name|name
parameter_list|,
name|LoginContext
name|loginContext
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Null login context"
argument_list|,
name|loginContext
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Logged in as {}:\n {}"
argument_list|,
name|name
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the JVM property to enable Kerberos debugging    */
DECL|method|enableKerberosDebugging ()
specifier|public
specifier|static
name|void
name|enableKerberosDebugging
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|AbstractSecureRegistryTest
operator|.
name|SUN_SECURITY_KRB5_DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the JVM property to enable Kerberos debugging    */
DECL|method|disableKerberosDebugging ()
specifier|public
specifier|static
name|void
name|disableKerberosDebugging
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|AbstractSecureRegistryTest
operator|.
name|SUN_SECURITY_KRB5_DEBUG
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
comment|/**    * General code to validate bits of a component/service entry built iwth    * {@link #addSampleEndpoints(ServiceRecord, String)}    * @param record instance to check    */
DECL|method|validateEntry (ServiceRecord record)
specifier|public
specifier|static
name|void
name|validateEntry
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"null service record"
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Endpoint
argument_list|>
name|endpoints
init|=
name|record
operator|.
name|external
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|endpoints
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Endpoint
name|webhdfs
init|=
name|findEndpoint
argument_list|(
name|record
argument_list|,
name|API_WEBHDFS
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|API_WEBHDFS
argument_list|,
name|webhdfs
operator|.
name|api
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_URI
argument_list|,
name|webhdfs
operator|.
name|addressType
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ProtocolTypes
operator|.
name|PROTOCOL_REST
argument_list|,
name|webhdfs
operator|.
name|protocolType
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|addressList
init|=
name|webhdfs
operator|.
name|addresses
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|url
init|=
name|addressList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
name|url
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|addr
operator|.
name|contains
argument_list|(
literal|"http"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|addr
operator|.
name|contains
argument_list|(
literal|":8020"
argument_list|)
argument_list|)
expr_stmt|;
name|Endpoint
name|nnipc
init|=
name|findEndpoint
argument_list|(
name|record
argument_list|,
name|NNIPC
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong protocol in "
operator|+
name|nnipc
argument_list|,
name|ProtocolTypes
operator|.
name|PROTOCOL_THRIFT
argument_list|,
name|nnipc
operator|.
name|protocolType
argument_list|)
expr_stmt|;
name|Endpoint
name|ipc2
init|=
name|findEndpoint
argument_list|(
name|record
argument_list|,
name|IPC2
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ipc2
argument_list|)
expr_stmt|;
name|Endpoint
name|web
init|=
name|findEndpoint
argument_list|(
name|record
argument_list|,
name|HTTP_API
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|web
operator|.
name|addresses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|web
operator|.
name|addresses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that an endpoint matches the criteria    * @param endpoint endpoint to examine    * @param addressType expected address type    * @param protocolType expected protocol type    * @param api API    */
DECL|method|assertMatches (Endpoint endpoint, String addressType, String protocolType, String api)
specifier|public
specifier|static
name|void
name|assertMatches
parameter_list|(
name|Endpoint
name|endpoint
parameter_list|,
name|String
name|addressType
parameter_list|,
name|String
name|protocolType
parameter_list|,
name|String
name|api
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|addressType
argument_list|,
name|endpoint
operator|.
name|addressType
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|protocolType
argument_list|,
name|endpoint
operator|.
name|protocolType
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|api
argument_list|,
name|endpoint
operator|.
name|api
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert the records match.    * @param source record that was written    * @param resolved the one that resolved.    */
DECL|method|assertMatches (ServiceRecord source, ServiceRecord resolved)
specifier|public
specifier|static
name|void
name|assertMatches
parameter_list|(
name|ServiceRecord
name|source
parameter_list|,
name|ServiceRecord
name|resolved
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Null source record "
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Null resolved record "
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|source
operator|.
name|description
argument_list|,
name|resolved
operator|.
name|description
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|srcAttrs
init|=
name|source
operator|.
name|attributes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|resolvedAttrs
init|=
name|resolved
operator|.
name|attributes
argument_list|()
decl_stmt|;
name|String
name|sourceAsString
init|=
name|source
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|resolvedAsString
init|=
name|resolved
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count of attrs in \n"
operator|+
name|sourceAsString
operator|+
literal|"\nfrom\n"
operator|+
name|resolvedAsString
argument_list|,
name|srcAttrs
operator|.
name|size
argument_list|()
argument_list|,
name|resolvedAttrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|srcAttrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|attr
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"attribute "
operator|+
name|attr
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|resolved
operator|.
name|get
argument_list|(
name|attr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wrong external endpoint count"
argument_list|,
name|source
operator|.
name|external
operator|.
name|size
argument_list|()
argument_list|,
name|resolved
operator|.
name|external
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong external endpoint count"
argument_list|,
name|source
operator|.
name|internal
operator|.
name|size
argument_list|()
argument_list|,
name|resolved
operator|.
name|internal
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Find an endpoint in a record or fail,    * @param record record    * @param api API    * @param external external?    * @param addressElements expected # of address elements?    * @param addressTupleSize expected size of a type    * @return the endpoint.    */
DECL|method|findEndpoint (ServiceRecord record, String api, boolean external, int addressElements, int addressTupleSize)
specifier|public
specifier|static
name|Endpoint
name|findEndpoint
parameter_list|(
name|ServiceRecord
name|record
parameter_list|,
name|String
name|api
parameter_list|,
name|boolean
name|external
parameter_list|,
name|int
name|addressElements
parameter_list|,
name|int
name|addressTupleSize
parameter_list|)
block|{
name|Endpoint
name|epr
init|=
name|external
condition|?
name|record
operator|.
name|getExternalEndpoint
argument_list|(
name|api
argument_list|)
else|:
name|record
operator|.
name|getInternalEndpoint
argument_list|(
name|api
argument_list|)
decl_stmt|;
if|if
condition|(
name|epr
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"wrong # of addresses"
argument_list|,
name|addressElements
argument_list|,
name|epr
operator|.
name|addresses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong # of elements in an address tuple"
argument_list|,
name|addressTupleSize
argument_list|,
name|epr
operator|.
name|addresses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|epr
return|;
block|}
name|List
argument_list|<
name|Endpoint
argument_list|>
name|endpoints
init|=
name|external
condition|?
name|record
operator|.
name|external
else|:
name|record
operator|.
name|internal
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Endpoint
name|endpoint
range|:
name|endpoints
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
operator|.
name|append
argument_list|(
name|endpoint
argument_list|)
operator|.
name|append
argument_list|(
literal|"\" "
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Did not find "
operator|+
name|api
operator|+
literal|" in endpoints "
operator|+
name|builder
argument_list|)
expr_stmt|;
comment|// never reached; here to keep the compiler happy
return|return
literal|null
return|;
block|}
comment|/**    * Log a record    * @param name record name    * @param record details    * @throws IOException only if something bizarre goes wrong marshalling    * a record.    */
DECL|method|logRecord (String name, ServiceRecord record)
specifier|public
specifier|static
name|void
name|logRecord
parameter_list|(
name|String
name|name
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" {} = \n{}\n"
argument_list|,
name|name
argument_list|,
name|recordMarshal
operator|.
name|toJson
argument_list|(
name|record
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a service entry with the sample endpoints    * @param persistence persistence policy    * @return the record    * @throws IOException on a failure    */
DECL|method|buildExampleServiceEntry (String persistence)
specifier|public
specifier|static
name|ServiceRecord
name|buildExampleServiceEntry
parameter_list|(
name|String
name|persistence
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|ServiceRecord
name|record
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_ID
argument_list|,
literal|"example-0001"
argument_list|)
expr_stmt|;
name|record
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_PERSISTENCE
argument_list|,
name|persistence
argument_list|)
expr_stmt|;
name|addSampleEndpoints
argument_list|(
name|record
argument_list|,
literal|"namenode"
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
comment|/**    * Add some endpoints    * @param entry entry    */
DECL|method|addSampleEndpoints (ServiceRecord entry, String hostname)
specifier|public
specifier|static
name|void
name|addSampleEndpoints
parameter_list|(
name|ServiceRecord
name|entry
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|assertNotNull
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
name|entry
operator|.
name|addExternalEndpoint
argument_list|(
name|webEndpoint
argument_list|(
name|HTTP_API
argument_list|,
operator|new
name|URI
argument_list|(
literal|"http"
argument_list|,
name|hostname
operator|+
literal|":80"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|addExternalEndpoint
argument_list|(
name|restEndpoint
argument_list|(
name|API_WEBHDFS
argument_list|,
operator|new
name|URI
argument_list|(
literal|"http"
argument_list|,
name|hostname
operator|+
literal|":8020"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Endpoint
name|endpoint
init|=
name|ipcEndpoint
argument_list|(
name|API_HDFS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|endpoint
operator|.
name|addresses
operator|.
name|add
argument_list|(
name|RegistryTypeUtils
operator|.
name|hostnamePortPair
argument_list|(
name|hostname
argument_list|,
literal|8030
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|addInternalEndpoint
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|localhost
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|8050
argument_list|)
decl_stmt|;
name|entry
operator|.
name|addInternalEndpoint
argument_list|(
name|inetAddrEndpoint
argument_list|(
name|NNIPC
argument_list|,
name|ProtocolTypes
operator|.
name|PROTOCOL_THRIFT
argument_list|,
literal|"localhost"
argument_list|,
literal|8050
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|addInternalEndpoint
argument_list|(
name|RegistryTypeUtils
operator|.
name|ipcEndpoint
argument_list|(
name|IPC2
argument_list|,
name|localhost
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Describe the stage in the process with a box around it -so as    * to highlight it in test logs    * @param log log to use    * @param text text    * @param args logger args    */
DECL|method|describe (Logger log, String text, Object...args)
specifier|public
specifier|static
name|void
name|describe
parameter_list|(
name|Logger
name|log
parameter_list|,
name|String
name|text
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"\n======================================="
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|text
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"=======================================\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * log out from a context if non-null ... exceptions are caught and logged    * @param login login context    * @return null, always    */
DECL|method|logout (LoginContext login)
specifier|public
specifier|static
name|LoginContext
name|logout
parameter_list|(
name|LoginContext
name|login
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|login
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Logging out login context {}"
argument_list|,
name|login
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|login
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception logging out: {}"
argument_list|,
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Login via a UGI. Requres UGI to have been set up    * @param user username    * @param keytab keytab to list    * @return the UGI    * @throws IOException    */
DECL|method|loginUGI (String user, File keytab)
specifier|public
specifier|static
name|UserGroupInformation
name|loginUGI
parameter_list|(
name|String
name|user
parameter_list|,
name|File
name|keytab
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Logging in as {} from {}"
argument_list|,
name|user
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
return|return
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|user
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createRecord (String persistence)
specifier|public
specifier|static
name|ServiceRecord
name|createRecord
parameter_list|(
name|String
name|persistence
parameter_list|)
block|{
return|return
name|createRecord
argument_list|(
literal|"01"
argument_list|,
name|persistence
argument_list|,
literal|"description"
argument_list|)
return|;
block|}
DECL|method|createRecord (String id, String persistence, String description)
specifier|public
specifier|static
name|ServiceRecord
name|createRecord
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|persistence
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|ServiceRecord
name|serviceRecord
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|serviceRecord
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|serviceRecord
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|serviceRecord
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_PERSISTENCE
argument_list|,
name|persistence
argument_list|)
expr_stmt|;
return|return
name|serviceRecord
return|;
block|}
DECL|method|createRecord (String id, String persistence, String description, String data)
specifier|public
specifier|static
name|ServiceRecord
name|createRecord
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|persistence
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|data
parameter_list|)
block|{
return|return
name|createRecord
argument_list|(
name|id
argument_list|,
name|persistence
argument_list|,
name|description
argument_list|)
return|;
block|}
block|}
end_class

end_unit

