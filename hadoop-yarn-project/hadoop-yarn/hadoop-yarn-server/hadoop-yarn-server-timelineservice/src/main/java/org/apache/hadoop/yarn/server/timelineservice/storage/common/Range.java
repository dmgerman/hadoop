begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
comment|/**  * Encapsulates a range with start and end indices.  */
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
DECL|class|Range
specifier|public
class|class
name|Range
block|{
DECL|field|startIdx
specifier|private
specifier|final
name|int
name|startIdx
decl_stmt|;
DECL|field|endIdx
specifier|private
specifier|final
name|int
name|endIdx
decl_stmt|;
comment|/**    * Defines a range from start index (inclusive) to end index (exclusive).    *    * @param start    *          Starting index position    * @param end    *          Ending index position (exclusive)    */
DECL|method|Range (int start, int end)
specifier|public
name|Range
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|end
operator|<
name|start
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid range, required that: 0<= start<= end; start="
operator|+
name|start
operator|+
literal|", end="
operator|+
name|end
argument_list|)
throw|;
block|}
name|this
operator|.
name|startIdx
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|endIdx
operator|=
name|end
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|startIdx
return|;
block|}
DECL|method|end ()
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|endIdx
return|;
block|}
DECL|method|length ()
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|endIdx
operator|-
name|startIdx
return|;
block|}
block|}
end_class

end_unit

