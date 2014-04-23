begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|hdfs
operator|.
name|DistributedFileSystem
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HDFSConcat
specifier|public
class|class
name|HDFSConcat
block|{
DECL|field|def_uri
specifier|private
specifier|final
specifier|static
name|String
name|def_uri
init|=
literal|"hdfs://localhost:9000"
decl_stmt|;
DECL|method|main (String... args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage HDFSConcat target srcs.."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"fs.default.name"
argument_list|,
name|def_uri
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
index|[]
name|srcs
init|=
operator|new
name|Path
index|[
name|args
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|srcs
index|[
name|i
operator|-
literal|1
index|]
operator|=
operator|new
name|Path
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|dfs
operator|.
name|concat
argument_list|(
operator|new
name|Path
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|srcs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

