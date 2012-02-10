begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|mapred
operator|.
name|InputSplit
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|CombineFileSplit
specifier|public
class|class
name|CombineFileSplit
extends|extends
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
name|CombineFileSplit
implements|implements
name|InputSplit
block|{
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
DECL|method|CombineFileSplit ()
specifier|public
name|CombineFileSplit
parameter_list|()
block|{   }
DECL|method|CombineFileSplit (JobConf job, Path[] files, long[] start, long[] lengths, String[] locations)
specifier|public
name|CombineFileSplit
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
index|[]
name|files
parameter_list|,
name|long
index|[]
name|start
parameter_list|,
name|long
index|[]
name|lengths
parameter_list|,
name|String
index|[]
name|locations
parameter_list|)
block|{
name|super
argument_list|(
name|files
argument_list|,
name|start
argument_list|,
name|lengths
argument_list|,
name|locations
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
block|}
DECL|method|CombineFileSplit (JobConf job, Path[] files, long[] lengths)
specifier|public
name|CombineFileSplit
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
index|[]
name|files
parameter_list|,
name|long
index|[]
name|lengths
parameter_list|)
block|{
name|super
argument_list|(
name|files
argument_list|,
name|lengths
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
block|}
comment|/**    * Copy constructor    */
DECL|method|CombineFileSplit (CombineFileSplit old)
specifier|public
name|CombineFileSplit
parameter_list|(
name|CombineFileSplit
name|old
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
DECL|method|getJob ()
specifier|public
name|JobConf
name|getJob
parameter_list|()
block|{
return|return
name|job
return|;
block|}
block|}
end_class

end_unit

