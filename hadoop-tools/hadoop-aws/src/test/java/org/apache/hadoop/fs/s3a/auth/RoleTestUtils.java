begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
name|auth
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|AccessDeniedException
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
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
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|FileSystem
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
name|Path
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|touch
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
name|*
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
name|S3ATestUtils
operator|.
name|disableFilesystemCaching
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
name|S3ATestUtils
operator|.
name|removeBaseAndBucketOverrides
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
name|auth
operator|.
name|RoleModel
operator|.
name|*
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
name|auth
operator|.
name|RolePolicies
operator|.
name|*
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
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
operator|.
name|DELEGATION_TOKEN_BINDING
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Helper class for testing roles.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RoleTestUtils
specifier|public
specifier|final
class|class
name|RoleTestUtils
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
name|RoleTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MODEL
specifier|private
specifier|static
specifier|final
name|RoleModel
name|MODEL
init|=
operator|new
name|RoleModel
argument_list|()
decl_stmt|;
comment|/** Example ARN of a role. */
DECL|field|ROLE_ARN_EXAMPLE
specifier|public
specifier|static
specifier|final
name|String
name|ROLE_ARN_EXAMPLE
init|=
literal|"arn:aws:iam::9878543210123:role/role-s3-restricted"
decl_stmt|;
comment|/** Deny GET requests to all buckets. */
DECL|field|DENY_S3_GET_OBJECT
specifier|public
specifier|static
specifier|final
name|Statement
name|DENY_S3_GET_OBJECT
init|=
name|statement
argument_list|(
literal|false
argument_list|,
name|S3_ALL_BUCKETS
argument_list|,
name|S3_GET_OBJECT
argument_list|)
decl_stmt|;
DECL|field|ALLOW_S3_GET_BUCKET_LOCATION
specifier|public
specifier|static
specifier|final
name|Statement
name|ALLOW_S3_GET_BUCKET_LOCATION
init|=
name|statement
argument_list|(
literal|true
argument_list|,
name|S3_ALL_BUCKETS
argument_list|,
name|S3_GET_BUCKET_LOCATION
argument_list|)
decl_stmt|;
comment|/**    * This is AWS policy removes read access from S3, leaves S3Guard access up.    * This will allow clients to use S3Guard list/HEAD operations, even    * the ability to write records, but not actually access the underlying    * data.    * The client does need {@link RolePolicies#S3_GET_BUCKET_LOCATION} to    * get the bucket location.    */
DECL|field|RESTRICTED_POLICY
specifier|public
specifier|static
specifier|final
name|Policy
name|RESTRICTED_POLICY
init|=
name|policy
argument_list|(
name|DENY_S3_GET_OBJECT
argument_list|,
name|STATEMENT_ALL_DDB
argument_list|,
name|ALLOW_S3_GET_BUCKET_LOCATION
argument_list|)
decl_stmt|;
DECL|method|RoleTestUtils ()
specifier|private
name|RoleTestUtils
parameter_list|()
block|{   }
comment|/**    * Bind the configuration's {@code ASSUMED_ROLE_POLICY} option to    * the given policy.    * @param conf configuration to patch    * @param policy policy to apply    * @return the modified configuration    * @throws JsonProcessingException JSON marshalling error    */
DECL|method|bindRolePolicy (final Configuration conf, final Policy policy)
specifier|public
specifier|static
name|Configuration
name|bindRolePolicy
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Policy
name|policy
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
name|String
name|p
init|=
name|MODEL
operator|.
name|toJson
argument_list|(
name|policy
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting role policy to policy of size {}:\n{}"
argument_list|,
name|p
operator|.
name|length
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ASSUMED_ROLE_POLICY
argument_list|,
name|p
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Wrap a set of statements with a policy and bind the configuration's    * {@code ASSUMED_ROLE_POLICY} option to it.    * @param conf configuration to patch    * @param statements statements to aggregate    * @return the modified configuration    * @throws JsonProcessingException JSON marshalling error    */
DECL|method|bindRolePolicyStatements ( final Configuration conf, final Statement... statements)
specifier|public
specifier|static
name|Configuration
name|bindRolePolicyStatements
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Statement
modifier|...
name|statements
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
return|return
name|bindRolePolicy
argument_list|(
name|conf
argument_list|,
name|policy
argument_list|(
name|statements
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Try to delete a file, verify that it is not allowed.    * @param fs filesystem    * @param path path    */
DECL|method|assertDeleteForbidden (final FileSystem fs, final Path path)
specifier|public
specifier|static
name|void
name|assertDeleteForbidden
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|AccessDeniedException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
parameter_list|()
lambda|->
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Try to touch a file, verify that it is not allowed.    * @param fs filesystem    * @param path path    */
DECL|method|assertTouchForbidden (final FileSystem fs, final Path path)
specifier|public
specifier|static
name|void
name|assertTouchForbidden
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|AccessDeniedException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
literal|"Caller could create file at "
operator|+
name|path
argument_list|,
parameter_list|()
lambda|->
block|{
name|touch
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a config for an assumed role; it also disables FS caching.    * @param srcConf source config: this is not modified    * @param roleARN ARN of role    * @return the new configuration    */
DECL|method|newAssumedRoleConfig ( final Configuration srcConf, final String roleARN)
specifier|public
specifier|static
name|Configuration
name|newAssumedRoleConfig
parameter_list|(
specifier|final
name|Configuration
name|srcConf
parameter_list|,
specifier|final
name|String
name|roleARN
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|srcConf
argument_list|)
decl_stmt|;
name|removeBaseAndBucketOverrides
argument_list|(
name|conf
argument_list|,
name|DELEGATION_TOKEN_BINDING
argument_list|,
name|ASSUMED_ROLE_ARN
argument_list|,
name|AWS_CREDENTIALS_PROVIDER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|AssumedRoleCredentialProvider
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ASSUMED_ROLE_ARN
argument_list|,
name|roleARN
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ASSUMED_ROLE_SESSION_NAME
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ASSUMED_ROLE_SESSION_DURATION
argument_list|,
literal|"15m"
argument_list|)
expr_stmt|;
name|disableFilesystemCaching
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Assert that an operation is forbidden.    * @param<T> type of closure    * @param contained contained text, may be null    * @param eval closure to evaluate    * @return the access denied exception    * @throws Exception any other exception    */
DECL|method|forbidden ( final String contained, final Callable<T> eval)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|AccessDeniedException
name|forbidden
parameter_list|(
specifier|final
name|String
name|contained
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|eval
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|forbidden
argument_list|(
literal|""
argument_list|,
name|contained
argument_list|,
name|eval
argument_list|)
return|;
block|}
comment|/**    * Assert that an operation is forbidden.    * @param<T> type of closure    * @param message error message    * @param contained contained text, may be null    * @param eval closure to evaluate    * @return the access denied exception    * @throws Exception any other exception    */
DECL|method|forbidden ( final String message, final String contained, final Callable<T> eval)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|AccessDeniedException
name|forbidden
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|String
name|contained
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|eval
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|intercept
argument_list|(
name|AccessDeniedException
operator|.
name|class
argument_list|,
name|contained
argument_list|,
name|message
argument_list|,
name|eval
argument_list|)
return|;
block|}
comment|/**    * Get the Assumed role referenced by ASSUMED_ROLE_ARN;    * skip the test if it is unset.    * @param conf config    * @return the string    */
DECL|method|probeForAssumedRoleARN (Configuration conf)
specifier|public
specifier|static
name|String
name|probeForAssumedRoleARN
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|arn
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|ASSUMED_ROLE_ARN
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|"No ARN defined in "
operator|+
name|ASSUMED_ROLE_ARN
argument_list|,
operator|!
name|arn
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|arn
return|;
block|}
comment|/**    * Assert that credentials are equal without printing secrets.    * Different assertions will have different message details.    * @param message message to use as base of error.    * @param expected expected credentials    * @param actual actual credentials.    */
DECL|method|assertCredentialsEqual (final String message, final MarshalledCredentials expected, final MarshalledCredentials actual)
specifier|public
specifier|static
name|void
name|assertCredentialsEqual
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|MarshalledCredentials
name|expected
parameter_list|,
specifier|final
name|MarshalledCredentials
name|actual
parameter_list|)
block|{
comment|// DO NOT use assertEquals() here, as that could print a secret to
comment|// the test report.
name|assertEquals
argument_list|(
name|message
operator|+
literal|": access key"
argument_list|,
name|expected
operator|.
name|getAccessKey
argument_list|()
argument_list|,
name|actual
operator|.
name|getAccessKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|+
literal|": secret key"
argument_list|,
name|expected
operator|.
name|getSecretKey
argument_list|()
operator|.
name|equals
argument_list|(
name|actual
operator|.
name|getSecretKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|": session token"
argument_list|,
name|expected
operator|.
name|getSessionToken
argument_list|()
argument_list|,
name|actual
operator|.
name|getSessionToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parallel-touch a set of files in the destination directory.    * @param fs filesystem    * @param destDir destination    * @param range range 1..range inclusive of files to create.    * @return the list of paths created.    */
DECL|method|touchFiles (final FileSystem fs, final Path destDir, final int range)
specifier|public
specifier|static
name|List
argument_list|<
name|Path
argument_list|>
name|touchFiles
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|destDir
parameter_list|,
specifier|final
name|int
name|range
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|paths
init|=
name|IntStream
operator|.
name|rangeClosed
argument_list|(
literal|1
argument_list|,
name|range
argument_list|)
operator|.
name|mapToObj
argument_list|(
parameter_list|(
name|i
parameter_list|)
lambda|->
operator|new
name|Path
argument_list|(
name|destDir
argument_list|,
literal|"file-"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|touch
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
block|}
end_class

end_unit

