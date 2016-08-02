begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api.types
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
import|;
end_import

begin_comment
comment|/**  * Serialized node entry information. Must be kept in sync with the protobuf equivalent.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|NodeEntryInformation
specifier|public
class|class
name|NodeEntryInformation
block|{
comment|/** incrementing counter of instances that failed */
DECL|field|failed
specifier|public
name|int
name|failed
decl_stmt|;
comment|/** Counter of "failed recently" events. */
DECL|field|failedRecently
specifier|public
name|int
name|failedRecently
decl_stmt|;
comment|/** timestamp of last use */
DECL|field|lastUsed
specifier|public
name|long
name|lastUsed
decl_stmt|;
comment|/** Number of live nodes. */
DECL|field|live
specifier|public
name|int
name|live
decl_stmt|;
comment|/** incrementing counter of instances that have been pre-empted. */
DECL|field|preempted
specifier|public
name|int
name|preempted
decl_stmt|;
comment|/** Priority */
DECL|field|priority
specifier|public
name|int
name|priority
decl_stmt|;
comment|/** instance explicitly requested on this node */
DECL|field|requested
specifier|public
name|int
name|requested
decl_stmt|;
comment|/** number of containers being released off this node */
DECL|field|releasing
specifier|public
name|int
name|releasing
decl_stmt|;
comment|/** incrementing counter of instances that failed to start */
DECL|field|startFailed
specifier|public
name|int
name|startFailed
decl_stmt|;
comment|/** number of starting instances */
DECL|field|starting
specifier|public
name|int
name|starting
decl_stmt|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"NodeEntryInformation{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"priority="
argument_list|)
operator|.
name|append
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", live="
argument_list|)
operator|.
name|append
argument_list|(
name|live
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", requested="
argument_list|)
operator|.
name|append
argument_list|(
name|requested
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", releasing="
argument_list|)
operator|.
name|append
argument_list|(
name|releasing
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", starting="
argument_list|)
operator|.
name|append
argument_list|(
name|starting
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", failed="
argument_list|)
operator|.
name|append
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", failedRecently="
argument_list|)
operator|.
name|append
argument_list|(
name|failedRecently
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", startFailed="
argument_list|)
operator|.
name|append
argument_list|(
name|startFailed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", preempted="
argument_list|)
operator|.
name|append
argument_list|(
name|preempted
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", lastUsed="
argument_list|)
operator|.
name|append
argument_list|(
name|lastUsed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

