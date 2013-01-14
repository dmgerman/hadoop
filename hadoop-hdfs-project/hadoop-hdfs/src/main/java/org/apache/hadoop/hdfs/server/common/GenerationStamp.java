begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
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
name|util
operator|.
name|SequentialNumber
import|;
end_import

begin_comment
comment|/****************************************************************  * A GenerationStamp is a Hadoop FS primitive, identified by a long.  ****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|GenerationStamp
specifier|public
class|class
name|GenerationStamp
extends|extends
name|SequentialNumber
block|{
comment|/**    * The last reserved generation stamp.    */
DECL|field|LAST_RESERVED_STAMP
specifier|public
specifier|static
specifier|final
name|long
name|LAST_RESERVED_STAMP
init|=
literal|1000L
decl_stmt|;
comment|/**    * Generation stamp of blocks that pre-date the introduction    * of a generation stamp.    */
DECL|field|GRANDFATHER_GENERATION_STAMP
specifier|public
specifier|static
specifier|final
name|long
name|GRANDFATHER_GENERATION_STAMP
init|=
literal|0
decl_stmt|;
comment|/**    * Create a new instance, initialized to {@link #LAST_RESERVED_STAMP}.    */
DECL|method|GenerationStamp ()
specifier|public
name|GenerationStamp
parameter_list|()
block|{
name|super
argument_list|(
name|LAST_RESERVED_STAMP
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

