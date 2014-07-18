begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
package|;
end_package

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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Utility class to load KMS configuration files.  */
end_comment

begin_class
DECL|class|KMSConfiguration
specifier|public
class|class
name|KMSConfiguration
block|{
DECL|field|KMS_CONFIG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|KMS_CONFIG_DIR
init|=
literal|"kms.config.dir"
decl_stmt|;
DECL|field|KMS_SITE_XML
specifier|public
specifier|static
specifier|final
name|String
name|KMS_SITE_XML
init|=
literal|"kms-site.xml"
decl_stmt|;
DECL|field|KMS_ACLS_XML
specifier|public
specifier|static
specifier|final
name|String
name|KMS_ACLS_XML
init|=
literal|"kms-acls.xml"
decl_stmt|;
DECL|field|CONFIG_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_PREFIX
init|=
literal|"hadoop.kms."
decl_stmt|;
comment|// Property to Enable/Disable Caching
DECL|field|KEY_CACHE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CACHE_ENABLE
init|=
name|CONFIG_PREFIX
operator|+
literal|"cache.enable"
decl_stmt|;
comment|// Timeout for the Key and Metadata Cache
DECL|field|KEY_CACHE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CACHE_TIMEOUT_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"cache.timeout.ms"
decl_stmt|;
comment|// TImeout for the Current Key cache
DECL|field|CURR_KEY_CACHE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CURR_KEY_CACHE_TIMEOUT_KEY
init|=
name|CONFIG_PREFIX
operator|+
literal|"current.key.cache.timeout.ms"
decl_stmt|;
DECL|field|KEY_CACHE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|KEY_CACHE_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|// 10 mins
DECL|field|KEY_CACHE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|KEY_CACHE_TIMEOUT_DEFAULT
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 30 secs
DECL|field|CURR_KEY_CACHE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|CURR_KEY_CACHE_TIMEOUT_DEFAULT
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
DECL|method|getConfiguration (boolean loadHadoopDefaults, String ... resources)
specifier|static
name|Configuration
name|getConfiguration
parameter_list|(
name|boolean
name|loadHadoopDefaults
parameter_list|,
name|String
modifier|...
name|resources
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|loadHadoopDefaults
argument_list|)
decl_stmt|;
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMS_CONFIG_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|confDir
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|KMS_CONFIG_DIR
operator|+
literal|"' must be an absolute path: "
operator|+
name|confDir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|confDir
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|confDir
operator|+=
literal|"/"
expr_stmt|;
block|}
for|for
control|(
name|String
name|resource
range|:
name|resources
control|)
block|{
name|conf
operator|.
name|addResource
argument_list|(
operator|new
name|URL
argument_list|(
literal|"file://"
operator|+
name|confDir
operator|+
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|resource
range|:
name|resources
control|)
block|{
name|conf
operator|.
name|addResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conf
return|;
block|}
DECL|method|getKMSConf ()
specifier|public
specifier|static
name|Configuration
name|getKMSConf
parameter_list|()
block|{
return|return
name|getConfiguration
argument_list|(
literal|true
argument_list|,
literal|"core-site.xml"
argument_list|,
name|KMS_SITE_XML
argument_list|)
return|;
block|}
DECL|method|getACLsConf ()
specifier|public
specifier|static
name|Configuration
name|getACLsConf
parameter_list|()
block|{
return|return
name|getConfiguration
argument_list|(
literal|false
argument_list|,
name|KMS_ACLS_XML
argument_list|)
return|;
block|}
DECL|method|isACLsFileNewer (long time)
specifier|public
specifier|static
name|boolean
name|isACLsFileNewer
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|boolean
name|newer
init|=
literal|false
decl_stmt|;
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|KMS_CONFIG_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|confDir
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|KMS_CONFIG_DIR
operator|+
literal|"' must be an absolute path: "
operator|+
name|confDir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|confDir
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|confDir
operator|+=
literal|"/"
expr_stmt|;
block|}
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
name|KMS_ACLS_XML
argument_list|)
decl_stmt|;
comment|// at least 100ms newer than time, we do this to ensure the file
comment|// has been properly closed/flushed
name|newer
operator|=
name|f
operator|.
name|lastModified
argument_list|()
operator|-
name|time
operator|>
literal|100
expr_stmt|;
block|}
return|return
name|newer
return|;
block|}
block|}
end_class

end_unit

