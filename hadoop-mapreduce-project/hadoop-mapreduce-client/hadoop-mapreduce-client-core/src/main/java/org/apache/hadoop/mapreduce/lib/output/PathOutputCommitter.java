begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.output
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|output
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
name|mapreduce
operator|.
name|JobContext
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
name|mapreduce
operator|.
name|OutputCommitter
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
name|mapreduce
operator|.
name|TaskAttemptContext
import|;
end_import

begin_comment
comment|/**  * A committer which somehow commits data written to a working directory  * to the final directory during the commit process. The reference  * implementation of this is the {@link FileOutputCommitter}.  *  * There are two constructors, both of which do nothing but long and  * validate their arguments.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PathOutputCommitter
specifier|public
specifier|abstract
class|class
name|PathOutputCommitter
extends|extends
name|OutputCommitter
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
name|PathOutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|JobContext
name|context
decl_stmt|;
comment|/**    * Constructor for a task attempt.    * Subclasses should provide a public constructor with this signature.    * @param outputPath output path: may be null    * @param context task context    * @throws IOException IO problem    */
DECL|method|PathOutputCommitter (Path outputPath, TaskAttemptContext context)
specifier|protected
name|PathOutputCommitter
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|,
literal|"Null context"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating committer with output path {} and task context"
operator|+
literal|" {}"
argument_list|,
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for a job attempt.    * Subclasses should provide a public constructor with this signature.    * @param outputPath output path: may be null    * @param context task context    * @throws IOException IO problem    */
DECL|method|PathOutputCommitter (Path outputPath, JobContext context)
specifier|protected
name|PathOutputCommitter
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|,
literal|"Null context"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating committer with output path {} and job context"
operator|+
literal|" {}"
argument_list|,
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the directory that the task should write results into.    * Warning: there's no guarantee that this work path is on the same    * FS as the final output, or that it's visible across machines.    * @return the work directory    * @throws IOException IO problem    */
DECL|method|getWorkPath ()
specifier|public
specifier|abstract
name|Path
name|getWorkPath
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PathOutputCommitter{context="
operator|+
name|context
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

