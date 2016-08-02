begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.avro
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|avro
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
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Compare two filenames by name; the older ones comes first  */
end_comment

begin_class
DECL|class|OlderFilesFirst
specifier|public
class|class
name|OlderFilesFirst
implements|implements
name|Comparator
argument_list|<
name|Path
argument_list|>
implements|,
name|Serializable
block|{
comment|/**    * Takes the ordering of path names from the normal string comparison    * and negates it, so that names that come after other names in     * the string sort come before here    * @param o1 leftmost     * @param o2 rightmost    * @return positive if o1&gt; o2     */
annotation|@
name|Override
DECL|method|compare (Path o1, Path o2)
specifier|public
name|int
name|compare
parameter_list|(
name|Path
name|o1
parameter_list|,
name|Path
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

