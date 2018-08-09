begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
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
name|com
operator|.
name|amazonaws
operator|.
name|ClientConfiguration
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
name|AWSCredentialsProvider
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
name|dynamodbv2
operator|.
name|AmazonDynamoDB
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
name|dynamodbv2
operator|.
name|AmazonDynamoDBClientBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|StringUtils
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
name|conf
operator|.
name|Configurable
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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|s3a
operator|.
name|S3AUtils
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|S3GUARD_DDB_REGION_KEY
import|;
end_import

begin_comment
comment|/**  * Interface to create a DynamoDB client.  *  * Implementation should be configured for setting and getting configuration.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|DynamoDBClientFactory
specifier|public
interface|interface
name|DynamoDBClientFactory
extends|extends
name|Configurable
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DynamoDBClientFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a DynamoDB client object from configuration.    *    * The DynamoDB client to create does not have to relate to any S3 buckets.    * All information needed to create a DynamoDB client is from the hadoop    * configuration. Specially, if the region is not configured, it will use the    * provided region parameter. If region is neither configured nor provided,    * it will indicate an error.    *    * @param defaultRegion the default region of the AmazonDynamoDB client    * @param bucket Optional bucket to use to look up per-bucket proxy secrets    * @param credentials credentials to use for authentication.    * @return a new DynamoDB client    * @throws IOException if any IO error happens    */
DECL|method|createDynamoDBClient (final String defaultRegion, final String bucket, final AWSCredentialsProvider credentials)
name|AmazonDynamoDB
name|createDynamoDBClient
parameter_list|(
specifier|final
name|String
name|defaultRegion
parameter_list|,
specifier|final
name|String
name|bucket
parameter_list|,
specifier|final
name|AWSCredentialsProvider
name|credentials
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The default implementation for creating an AmazonDynamoDB.    */
DECL|class|DefaultDynamoDBClientFactory
class|class
name|DefaultDynamoDBClientFactory
extends|extends
name|Configured
implements|implements
name|DynamoDBClientFactory
block|{
annotation|@
name|Override
DECL|method|createDynamoDBClient (String defaultRegion, final String bucket, final AWSCredentialsProvider credentials)
specifier|public
name|AmazonDynamoDB
name|createDynamoDBClient
parameter_list|(
name|String
name|defaultRegion
parameter_list|,
specifier|final
name|String
name|bucket
parameter_list|,
specifier|final
name|AWSCredentialsProvider
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getConf
argument_list|()
argument_list|,
literal|"Should have been configured before usage"
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
specifier|final
name|ClientConfiguration
name|awsConf
init|=
name|S3AUtils
operator|.
name|createAwsConf
argument_list|(
name|conf
argument_list|,
name|bucket
argument_list|)
decl_stmt|;
specifier|final
name|String
name|region
init|=
name|getRegion
argument_list|(
name|conf
argument_list|,
name|defaultRegion
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating DynamoDB client in region {}"
argument_list|,
name|region
argument_list|)
expr_stmt|;
return|return
name|AmazonDynamoDBClientBuilder
operator|.
name|standard
argument_list|()
operator|.
name|withCredentials
argument_list|(
name|credentials
argument_list|)
operator|.
name|withClientConfiguration
argument_list|(
name|awsConf
argument_list|)
operator|.
name|withRegion
argument_list|(
name|region
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Helper method to get and validate the AWS region for DynamoDBClient.      *      * @param conf configuration      * @param defaultRegion the default region      * @return configured region or else the provided default region      * @throws IOException if the region is not valid      */
DECL|method|getRegion (Configuration conf, String defaultRegion)
specifier|static
name|String
name|getRegion
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|defaultRegion
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|region
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|S3GUARD_DDB_REGION_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|region
argument_list|)
condition|)
block|{
name|region
operator|=
name|defaultRegion
expr_stmt|;
block|}
try|try
block|{
name|Regions
operator|.
name|fromName
argument_list|(
name|region
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|NullPointerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid region specified: "
operator|+
name|region
operator|+
literal|"; "
operator|+
literal|"Region can be configured with "
operator|+
name|S3GUARD_DDB_REGION_KEY
operator|+
literal|": "
operator|+
name|validRegionsString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|region
return|;
block|}
DECL|method|validRegionsString ()
specifier|private
specifier|static
name|String
name|validRegionsString
parameter_list|()
block|{
specifier|final
name|String
name|delimiter
init|=
literal|", "
decl_stmt|;
name|Regions
index|[]
name|regions
init|=
name|Regions
operator|.
name|values
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|regions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|regions
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_interface

end_unit

