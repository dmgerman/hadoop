begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
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
comment|/**  * Utility class for tracking descent into the structure of the  * Visitor class (ImageVisitor, EditsVisitor etc.)  */
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
DECL|class|DepthCounter
specifier|public
class|class
name|DepthCounter
block|{
DECL|field|depth
specifier|private
name|int
name|depth
init|=
literal|0
decl_stmt|;
DECL|method|incLevel ()
specifier|public
name|void
name|incLevel
parameter_list|()
block|{
name|depth
operator|++
expr_stmt|;
block|}
DECL|method|decLevel ()
specifier|public
name|void
name|decLevel
parameter_list|()
block|{
if|if
condition|(
name|depth
operator|>=
literal|1
condition|)
name|depth
operator|--
expr_stmt|;
block|}
DECL|method|getLevel ()
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
block|}
end_class

end_unit

