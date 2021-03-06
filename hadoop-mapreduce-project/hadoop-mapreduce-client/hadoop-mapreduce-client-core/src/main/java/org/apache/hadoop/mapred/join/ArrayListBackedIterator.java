begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|join
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * This class provides an implementation of ResetableIterator. The  * implementation uses an {@link java.util.ArrayList} to store elements  * added to it, replaying them as requested.  * Prefer {@link StreamBackedIterator}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ArrayListBackedIterator
specifier|public
class|class
name|ArrayListBackedIterator
parameter_list|<
name|X
extends|extends
name|Writable
parameter_list|>
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
name|join
operator|.
name|ArrayListBackedIterator
argument_list|<
name|X
argument_list|>
implements|implements
name|ResetableIterator
argument_list|<
name|X
argument_list|>
block|{
DECL|method|ArrayListBackedIterator ()
specifier|public
name|ArrayListBackedIterator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|ArrayListBackedIterator (ArrayList<X> data)
specifier|public
name|ArrayListBackedIterator
parameter_list|(
name|ArrayList
argument_list|<
name|X
argument_list|>
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

