begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Random
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
operator|.
name|OperationOutput
operator|.
name|OutputType
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
comment|/**  * Operation which selects a random directory and attempts to create that  * directory.  *   * This operation will capture statistics on success the time taken to create  * that directory and the number of successful creations that occurred and on  * failure or error it will capture the number of failures and the amount of  * time taken to fail  */
end_comment

begin_class
DECL|class|MkdirOp
class|class
name|MkdirOp
extends|extends
name|Operation
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
name|MkdirOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MkdirOp (ConfigExtractor cfg, Random rnd)
name|MkdirOp
parameter_list|(
name|ConfigExtractor
name|cfg
parameter_list|,
name|Random
name|rnd
parameter_list|)
block|{
name|super
argument_list|(
name|MkdirOp
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|cfg
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the directory name to try to make    *     * @return Path    */
DECL|method|getDirectory ()
specifier|protected
name|Path
name|getDirectory
parameter_list|()
block|{
name|Path
name|dir
init|=
name|getFinder
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
return|return
name|dir
return|;
block|}
annotation|@
name|Override
comment|// Operation
DECL|method|run (FileSystem fs)
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|out
init|=
name|super
operator|.
name|run
argument_list|(
name|fs
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|dir
init|=
name|getDirectory
argument_list|()
decl_stmt|;
name|boolean
name|mkRes
init|=
literal|false
decl_stmt|;
name|long
name|timeTaken
init|=
literal|0
decl_stmt|;
block|{
name|long
name|startTime
init|=
name|Timer
operator|.
name|now
argument_list|()
decl_stmt|;
name|mkRes
operator|=
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|timeTaken
operator|=
name|Timer
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
comment|// log stats
if|if
condition|(
name|mkRes
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|OK_TIME_TAKEN
argument_list|,
name|timeTaken
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|SUCCESSES
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Made directory "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|FAILURES
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not make "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|NOT_FOUND
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error with mkdir"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|FAILURES
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error with mkdir"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

