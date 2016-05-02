begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Class to tell the size of a path on windows.  * Rather than shelling out, on windows this uses DUHelper.getFolderUsage  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|WindowsGetSpaceUsed
specifier|public
class|class
name|WindowsGetSpaceUsed
extends|extends
name|CachingGetSpaceUsed
block|{
DECL|method|WindowsGetSpaceUsed (CachingGetSpaceUsed.Builder builder)
specifier|public
name|WindowsGetSpaceUsed
parameter_list|(
name|CachingGetSpaceUsed
operator|.
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|builder
operator|.
name|getPath
argument_list|()
argument_list|,
name|builder
operator|.
name|getInterval
argument_list|()
argument_list|,
name|builder
operator|.
name|getInitialUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override to hook in DUHelper class.    */
annotation|@
name|Override
DECL|method|refresh ()
specifier|protected
name|void
name|refresh
parameter_list|()
block|{
name|used
operator|.
name|set
argument_list|(
name|DUHelper
operator|.
name|getFolderUsage
argument_list|(
name|getDirPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

