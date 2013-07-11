begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.startupprogress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|startupprogress
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

begin_comment
comment|/**  * Abstract base of internal data structures used for tracking progress.  For  * primitive long properties, {@link Long#MIN_VALUE} is used as a sentinel value  * to indicate that the property is undefined.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractTracking
specifier|abstract
class|class
name|AbstractTracking
implements|implements
name|Cloneable
block|{
DECL|field|beginTime
name|long
name|beginTime
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|endTime
name|long
name|endTime
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
comment|/**    * Subclass instances may call this method during cloning to copy the values of    * all properties stored in this base class.    *     * @param dest AbstractTracking destination for copying properties    */
DECL|method|copy (AbstractTracking dest)
specifier|protected
name|void
name|copy
parameter_list|(
name|AbstractTracking
name|dest
parameter_list|)
block|{
name|dest
operator|.
name|beginTime
operator|=
name|beginTime
expr_stmt|;
name|dest
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
block|}
block|}
end_class

end_unit

