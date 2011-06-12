begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|streaming
operator|.
name|Environment
import|;
end_import

begin_comment
comment|/**      Used to test the usage of external applications without adding     platform-specific dependencies.  */
end_comment

begin_class
DECL|class|StreamAggregate
specifier|public
class|class
name|StreamAggregate
extends|extends
name|TrApp
block|{
DECL|method|StreamAggregate ()
specifier|public
name|StreamAggregate
parameter_list|()
block|{
name|super
argument_list|(
literal|'.'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
DECL|method|go ()
specifier|public
name|void
name|go
parameter_list|()
throws|throws
name|IOException
block|{
name|testParentJobConfToEnvVars
argument_list|()
expr_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|words
init|=
name|line
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|words
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|out
init|=
literal|"LongValueSum:"
operator|+
name|words
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
operator|+
literal|"\t"
operator|+
literal|"1"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|TrApp
name|app
init|=
operator|new
name|StreamAggregate
argument_list|()
decl_stmt|;
name|app
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

