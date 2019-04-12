begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsConfigKeys
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ScmOps
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
name|scm
operator|.
name|chillmode
operator|.
name|Precheck
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
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|server
operator|.
name|ServerUtils
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * SCM utility class.  */
end_comment

begin_class
DECL|class|ScmUtils
specifier|public
specifier|final
class|class
name|ScmUtils
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
name|ScmUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ScmUtils ()
specifier|private
name|ScmUtils
parameter_list|()
block|{   }
comment|/**    * Perform all prechecks for given scm operation.    *    * @param operation    * @param preChecks prechecks to be performed    */
DECL|method|preCheck (ScmOps operation, Precheck... preChecks)
specifier|public
specifier|static
name|void
name|preCheck
parameter_list|(
name|ScmOps
name|operation
parameter_list|,
name|Precheck
modifier|...
name|preChecks
parameter_list|)
throws|throws
name|SCMException
block|{
for|for
control|(
name|Precheck
name|preCheck
range|:
name|preChecks
control|)
block|{
name|preCheck
operator|.
name|check
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDBPath (Configuration conf, String dbDirectory)
specifier|public
specifier|static
name|File
name|getDBPath
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|dbDirectory
parameter_list|)
block|{
specifier|final
name|File
name|dbDirPath
init|=
name|ServerUtils
operator|.
name|getDirectoryFromConfig
argument_list|(
name|conf
argument_list|,
name|dbDirectory
argument_list|,
literal|"OM"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbDirPath
operator|!=
literal|null
condition|)
block|{
return|return
name|dbDirPath
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} is not configured. We recommend adding this setting. "
operator|+
literal|"Falling back to {} instead."
argument_list|,
name|dbDirectory
argument_list|,
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
expr_stmt|;
return|return
name|ServerUtils
operator|.
name|getOzoneMetaDirPath
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

