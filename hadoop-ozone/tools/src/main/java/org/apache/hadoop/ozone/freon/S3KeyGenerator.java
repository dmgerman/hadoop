begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|hdds
operator|.
name|cli
operator|.
name|HddsVersionProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|EnvironmentVariableCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|client
operator|.
name|builder
operator|.
name|AwsClientBuilder
operator|.
name|EndpointConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|regions
operator|.
name|Regions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3ClientBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|lang3
operator|.
name|RandomStringUtils
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
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_comment
comment|/**  * Generate random keys via the s3 interface.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"s3kg"
argument_list|,
name|aliases
operator|=
literal|"s3-key-generator"
argument_list|,
name|description
operator|=
literal|"Create random keys via the s3 interface."
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|showDefaultValues
operator|=
literal|true
argument_list|)
DECL|class|S3KeyGenerator
specifier|public
class|class
name|S3KeyGenerator
extends|extends
name|BaseFreonGenerator
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
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
name|S3KeyGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-b"
block|,
literal|"--bucket"
block|}
argument_list|,
name|description
operator|=
literal|"Name of the (S3!) bucket which contains the test data."
argument_list|,
name|defaultValue
operator|=
literal|"bucket1"
argument_list|)
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-s"
block|,
literal|"--size"
block|}
argument_list|,
name|description
operator|=
literal|"Size of the generated key (in bytes)"
argument_list|,
name|defaultValue
operator|=
literal|"10240"
argument_list|)
DECL|field|fileSize
specifier|private
name|int
name|fileSize
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-e"
block|,
literal|"--endpoint"
block|}
argument_list|,
name|description
operator|=
literal|"S3 HTTP endpoint"
argument_list|,
name|defaultValue
operator|=
literal|"http://localhost:9878"
argument_list|)
DECL|field|endpoint
specifier|private
name|String
name|endpoint
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
decl_stmt|;
DECL|field|content
specifier|private
name|String
name|content
decl_stmt|;
DECL|field|s3
specifier|private
name|AmazonS3
name|s3
decl_stmt|;
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|AmazonS3ClientBuilder
name|amazonS3ClientBuilder
init|=
name|AmazonS3ClientBuilder
operator|.
name|standard
argument_list|()
operator|.
name|withCredentials
argument_list|(
operator|new
name|EnvironmentVariableCredentialsProvider
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpoint
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|amazonS3ClientBuilder
operator|.
name|withPathStyleAccessEnabled
argument_list|(
literal|true
argument_list|)
operator|.
name|withEndpointConfiguration
argument_list|(
operator|new
name|EndpointConfiguration
argument_list|(
name|endpoint
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|amazonS3ClientBuilder
operator|.
name|withRegion
argument_list|(
name|Regions
operator|.
name|DEFAULT_REGION
argument_list|)
expr_stmt|;
block|}
name|s3
operator|=
name|amazonS3ClientBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|content
operator|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
name|fileSize
argument_list|)
expr_stmt|;
name|timer
operator|=
name|getMetrics
argument_list|()
operator|.
name|timer
argument_list|(
literal|"key-create"
argument_list|)
expr_stmt|;
name|runTests
argument_list|(
name|this
operator|::
name|createKey
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|createKey (long counter)
specifier|private
name|void
name|createKey
parameter_list|(
name|long
name|counter
parameter_list|)
throws|throws
name|Exception
block|{
name|timer
operator|.
name|time
argument_list|(
parameter_list|()
lambda|->
block|{
name|s3
operator|.
name|putObject
argument_list|(
name|bucketName
argument_list|,
name|generateObjectName
argument_list|(
name|counter
argument_list|)
argument_list|,
name|content
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

