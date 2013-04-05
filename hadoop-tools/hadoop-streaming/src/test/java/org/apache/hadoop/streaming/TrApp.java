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
comment|/** A minimal Java implementation of /usr/bin/tr.  *  Used to test the usage of external applications without adding  *  platform-specific dependencies.  *  Use TrApp as mapper only. For reducer, use TrAppReduce.  */
end_comment

begin_class
DECL|class|TrApp
specifier|public
class|class
name|TrApp
block|{
DECL|method|TrApp (char find, char replace)
specifier|public
name|TrApp
parameter_list|(
name|char
name|find
parameter_list|,
name|char
name|replace
parameter_list|)
block|{
name|this
operator|.
name|find
operator|=
name|find
expr_stmt|;
name|this
operator|.
name|replace
operator|=
name|replace
expr_stmt|;
block|}
DECL|method|testParentJobConfToEnvVars ()
name|void
name|testParentJobConfToEnvVars
parameter_list|()
throws|throws
name|IOException
block|{
name|env
operator|=
operator|new
name|Environment
argument_list|()
expr_stmt|;
comment|// test that some JobConf properties are exposed as expected
comment|// Note the dots translated to underscore:
comment|// property names have been escaped in PipeMapRed.safeEnvVarName()
name|expectDefined
argument_list|(
literal|"mapreduce_cluster_local_dir"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"mapreduce_map_output_key_class"
argument_list|,
literal|"org.apache.hadoop.io.Text"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"mapreduce_map_output_value_class"
argument_list|,
literal|"org.apache.hadoop.io.Text"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"mapreduce_task_ismap"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|expectDefined
argument_list|(
literal|"mapreduce_task_attempt_id"
argument_list|)
expr_stmt|;
name|expectDefined
argument_list|(
literal|"mapreduce_map_input_file"
argument_list|)
expr_stmt|;
name|expectDefined
argument_list|(
literal|"mapreduce_map_input_length"
argument_list|)
expr_stmt|;
name|expectDefined
argument_list|(
literal|"mapreduce_task_io_sort_factor"
argument_list|)
expr_stmt|;
comment|// the FileSplit context properties are not available in local hadoop..
comment|// so can't check them in this test.
block|}
comment|// this runs in a subprocess; won't use JUnit's assertTrue()
DECL|method|expect (String evName, String evVal)
name|void
name|expect
parameter_list|(
name|String
name|evName
parameter_list|,
name|String
name|evVal
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|got
init|=
name|env
operator|.
name|getProperty
argument_list|(
name|evName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|evVal
operator|.
name|equals
argument_list|(
name|got
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"FAIL evName="
operator|+
name|evName
operator|+
literal|" got="
operator|+
name|got
operator|+
literal|" expect="
operator|+
name|evVal
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
DECL|method|expectDefined (String evName)
name|void
name|expectDefined
parameter_list|(
name|String
name|evName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|got
init|=
name|env
operator|.
name|getProperty
argument_list|(
name|evName
argument_list|)
decl_stmt|;
if|if
condition|(
name|got
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"FAIL evName="
operator|+
name|evName
operator|+
literal|" is undefined. Expect defined."
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
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
name|out
init|=
name|line
operator|.
name|replace
argument_list|(
name|find
argument_list|,
name|replace
argument_list|)
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"reporter:counter:UserCounters,InputLines,1"
argument_list|)
expr_stmt|;
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
name|args
index|[
literal|0
index|]
operator|=
name|CUnescape
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|CUnescape
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|TrApp
name|app
init|=
operator|new
name|TrApp
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|args
index|[
literal|1
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|app
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
DECL|method|CUnescape (String s)
specifier|public
specifier|static
name|String
name|CUnescape
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"\\n"
argument_list|)
condition|)
block|{
return|return
literal|"\n"
return|;
block|}
else|else
block|{
return|return
name|s
return|;
block|}
block|}
DECL|field|find
name|char
name|find
decl_stmt|;
DECL|field|replace
name|char
name|replace
decl_stmt|;
DECL|field|env
name|Environment
name|env
decl_stmt|;
block|}
end_class

end_unit

