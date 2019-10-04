begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.staging
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
name|commit
operator|.
name|staging
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
comment|/**  * Internal staging committer constants.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|StagingCommitterConstants
specifier|public
specifier|final
class|class
name|StagingCommitterConstants
block|{
DECL|method|StagingCommitterConstants ()
specifier|private
name|StagingCommitterConstants
parameter_list|()
block|{   }
comment|/**    * The temporary path for staging data, if not explicitly set.    * By using an unqualified path, this will be qualified to be relative    * to the users' home directory, so protected from access for others.    */
DECL|field|FILESYSTEM_TEMP_PATH
specifier|public
specifier|static
specifier|final
name|String
name|FILESYSTEM_TEMP_PATH
init|=
literal|"tmp/staging"
decl_stmt|;
comment|/** Name of the root partition :{@value}. */
DECL|field|TABLE_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|TABLE_ROOT
init|=
literal|"table_root"
decl_stmt|;
comment|/**    * Filename used under {@code ~/${UUID}} for the staging files.    */
DECL|field|STAGING_UPLOADS
specifier|public
specifier|static
specifier|final
name|String
name|STAGING_UPLOADS
init|=
literal|"staging-uploads"
decl_stmt|;
comment|// Spark configuration keys
comment|/**    * The UUID for jobs: {@value}.    */
DECL|field|SPARK_WRITE_UUID
specifier|public
specifier|static
specifier|final
name|String
name|SPARK_WRITE_UUID
init|=
literal|"spark.sql.sources.writeJobUUID"
decl_stmt|;
comment|/**    * The App ID for jobs.    */
DECL|field|SPARK_APP_ID
specifier|public
specifier|static
specifier|final
name|String
name|SPARK_APP_ID
init|=
literal|"spark.app.id"
decl_stmt|;
DECL|field|JAVA_IO_TMPDIR
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_IO_TMPDIR
init|=
literal|"java.io.tmpdir"
decl_stmt|;
block|}
end_class

end_unit

