begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.appcatalog.application
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|application
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|AppEntry
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceState
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|KerberosPrincipal
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
name|service
operator|.
name|client
operator|.
name|ApiServiceClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonProcessingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|DeserializationFeature
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientHandlerException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|UniformInterfaceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|config
operator|.
name|ClientConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|config
operator|.
name|DefaultClientConfig
import|;
end_import

begin_comment
comment|/**  * Driver class for calling YARN Resource Manager REST API.  */
end_comment

begin_class
DECL|class|YarnServiceClient
specifier|public
class|class
name|YarnServiceClient
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|YarnServiceClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|getClientConfig ()
specifier|private
specifier|static
name|ClientConfig
name|getClientConfig
parameter_list|()
block|{
name|ClientConfig
name|config
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|getProperties
argument_list|()
operator|.
name|put
argument_list|(
name|ClientConfig
operator|.
name|PROPERTY_CHUNKED_ENCODING_SIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|config
operator|.
name|getProperties
argument_list|()
operator|.
name|put
argument_list|(
name|ClientConfig
operator|.
name|PROPERTY_BUFFER_RESPONSE_ENTITY_ON_EXCEPTION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
DECL|field|asc
specifier|private
name|ApiServiceClient
name|asc
decl_stmt|;
DECL|method|YarnServiceClient ()
specifier|public
name|YarnServiceClient
parameter_list|()
block|{
try|try
block|{
name|asc
operator|=
operator|new
name|ApiServiceClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error initialize YARN Service Client: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createApp (Service app)
specifier|public
name|void
name|createApp
parameter_list|(
name|Service
name|app
parameter_list|)
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ClientResponse
name|response
decl_stmt|;
try|try
block|{
name|boolean
name|useKerberos
init|=
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
decl_stmt|;
if|if
condition|(
name|useKerberos
condition|)
block|{
name|KerberosPrincipal
name|kerberos
init|=
operator|new
name|KerberosPrincipal
argument_list|()
decl_stmt|;
name|String
index|[]
name|temp
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"PRINCIPAL"
argument_list|)
operator|.
name|split
argument_list|(
literal|"@"
argument_list|)
decl_stmt|;
name|String
index|[]
name|temp2
init|=
name|temp
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|temp2
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"_HOST"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|temp
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|String
name|keytab
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"KEYTAB"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keytab
operator|.
name|startsWith
argument_list|(
literal|"file://"
argument_list|)
condition|)
block|{
name|keytab
operator|=
literal|"file://"
operator|+
name|keytab
expr_stmt|;
block|}
name|kerberos
operator|.
name|setPrincipalName
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|kerberos
operator|.
name|setKeytab
argument_list|(
name|keytab
argument_list|)
expr_stmt|;
name|app
operator|.
name|setKerberosPrincipal
argument_list|(
name|kerberos
argument_list|)
expr_stmt|;
block|}
name|response
operator|=
name|asc
operator|.
name|getApiClient
argument_list|()
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|app
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|>=
literal|299
condition|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed : HTTP error code : "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|" error: "
operator|+
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|ClientHandlerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in deploying application: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteApp (String appInstanceId)
specifier|public
name|void
name|deleteApp
parameter_list|(
name|String
name|appInstanceId
parameter_list|)
block|{
name|ClientResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|asc
operator|.
name|getApiClient
argument_list|(
name|asc
operator|.
name|getServicePath
argument_list|(
name|appInstanceId
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|>=
literal|299
condition|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed : HTTP error code : "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|" error: "
operator|+
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|ClientHandlerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in deleting application: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|restartApp (Service app)
specifier|public
name|void
name|restartApp
parameter_list|(
name|Service
name|app
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|appInstanceId
init|=
name|app
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|yarnFile
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|asc
operator|.
name|getApiClient
argument_list|(
name|asc
operator|.
name|getServicePath
argument_list|(
name|appInstanceId
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|yarnFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|>=
literal|299
condition|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed : HTTP error code : "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|" error: "
operator|+
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|ClientHandlerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in restarting application: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopApp (Service app)
specifier|public
name|void
name|stopApp
parameter_list|(
name|Service
name|app
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|appInstanceId
init|=
name|app
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|yarnFile
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|asc
operator|.
name|getApiClient
argument_list|(
name|asc
operator|.
name|getServicePath
argument_list|(
name|appInstanceId
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|yarnFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|>=
literal|299
condition|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed : HTTP error code : "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|" error: "
operator|+
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|ClientHandlerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in stopping application: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStatus (AppEntry entry)
specifier|public
name|void
name|getStatus
parameter_list|(
name|AppEntry
name|entry
parameter_list|)
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|appInstanceId
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Service
name|app
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|yarnFile
init|=
name|asc
operator|.
name|getApiClient
argument_list|(
name|asc
operator|.
name|getServicePath
argument_list|(
name|appInstanceId
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|app
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
name|yarnFile
argument_list|,
name|Service
operator|.
name|class
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setYarnfile
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in fetching application status: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|upgradeApp (Service app)
specifier|public
name|void
name|upgradeApp
parameter_list|(
name|Service
name|app
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|appInstanceId
init|=
name|app
operator|.
name|getName
argument_list|()
decl_stmt|;
name|app
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|EXPRESS_UPGRADING
argument_list|)
expr_stmt|;
name|String
name|yarnFile
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|asc
operator|.
name|getApiClient
argument_list|(
name|asc
operator|.
name|getServicePath
argument_list|(
name|appInstanceId
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|yarnFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|>=
literal|299
condition|)
block|{
name|String
name|message
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed : HTTP error code : "
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|" error: "
operator|+
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
decl||
name|ClientHandlerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in stopping application: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

