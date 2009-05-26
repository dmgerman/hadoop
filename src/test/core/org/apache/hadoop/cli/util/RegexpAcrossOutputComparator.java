begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Comparator for command line tests that attempts to find a regexp  * within the entire text returned by a command.  *  * This comparator differs from RegexpComparator in that it attempts  * to match the pattern within all of the text returned by the command,  * rather than matching against each line of the returned text.  This  * allows matching against patterns that span multiple lines.  */
end_comment

begin_class
DECL|class|RegexpAcrossOutputComparator
specifier|public
class|class
name|RegexpAcrossOutputComparator
extends|extends
name|ComparatorBase
block|{
annotation|@
name|Override
DECL|method|compare (String actual, String expected)
specifier|public
name|boolean
name|compare
parameter_list|(
name|String
name|actual
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|expected
argument_list|)
operator|.
name|matcher
argument_list|(
name|actual
argument_list|)
operator|.
name|find
argument_list|()
return|;
block|}
block|}
end_class

end_unit

