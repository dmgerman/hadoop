begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
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
name|util
operator|.
name|List
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
name|FileStatus
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
name|lib
operator|.
name|input
operator|.
name|FileInputFormat
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
name|lib
operator|.
name|input
operator|.
name|TextInputFormat
import|;
end_import

begin_comment
comment|/**  * A simple {@link TextInputFormat} that disables splitting of files. This is  * the {@link org.apache.hadoop.mapreduce.InputFormat} used by  * {@link AuditReplayMapper}.  */
end_comment

begin_class
DECL|class|NoSplitTextInputFormat
specifier|public
class|class
name|NoSplitTextInputFormat
extends|extends
name|TextInputFormat
block|{
annotation|@
name|Override
DECL|method|listStatus (JobContext context)
specifier|public
name|List
argument_list|<
name|FileStatus
argument_list|>
name|listStatus
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FileInputFormat
operator|.
name|INPUT_DIR
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|AuditReplayMapper
operator|.
name|INPUT_PATH_KEY
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|listStatus
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isSplitable (JobContext context, Path file)
specifier|public
name|boolean
name|isSplitable
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|Path
name|file
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

