begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|util
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
name|tools
operator|.
name|DistCp
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Utility class for DistCpTests  */
end_comment

begin_class
DECL|class|DistCpTestUtils
specifier|public
class|class
name|DistCpTestUtils
block|{
comment|/**     * Asserts the XAttrs returned by getXAttrs for a specific path match an     * expected set of XAttrs.     *     * @param path String path to check     * @param fs FileSystem to use for the path     * @param expectedXAttrs XAttr[] expected xAttrs     * @throws Exception if there is any error     */
DECL|method|assertXAttrs (Path path, FileSystem fs, Map<String, byte[]> expectedXAttrs)
specifier|public
specifier|static
name|void
name|assertXAttrs
parameter_list|(
name|Path
name|path
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|expectedXAttrs
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xAttrs
init|=
name|fs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|expectedXAttrs
operator|.
name|size
argument_list|()
argument_list|,
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|i
init|=
name|expectedXAttrs
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|e
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|byte
index|[]
name|value
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|xAttrs
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|&&
name|xAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertArrayEquals
argument_list|(
name|value
argument_list|,
name|xAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Runs distcp from src to dst, preserving XAttrs. Asserts the    * expected exit code.    *    * @param exitCode expected exit code    * @param src distcp src path    * @param dst distcp destination    * @param options distcp command line options    * @param conf Configuration to use    * @throws Exception if there is any error    */
DECL|method|assertRunDistCp (int exitCode, String src, String dst, String options, Configuration conf)
specifier|public
specifier|static
name|void
name|assertRunDistCp
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|String
name|options
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|assertRunDistCp
argument_list|(
name|exitCode
argument_list|,
name|src
argument_list|,
name|dst
argument_list|,
name|options
operator|==
literal|null
condition|?
operator|new
name|String
index|[
literal|0
index|]
else|:
name|options
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRunDistCp (int exitCode, String src, String dst, String[] options, Configuration conf)
specifier|private
specifier|static
name|void
name|assertRunDistCp
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|String
index|[]
name|options
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|DistCp
name|distCp
init|=
operator|new
name|DistCp
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
index|[]
name|optsArr
init|=
operator|new
name|String
index|[
name|options
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|options
argument_list|,
literal|0
argument_list|,
name|optsArr
argument_list|,
literal|0
argument_list|,
name|options
operator|.
name|length
argument_list|)
expr_stmt|;
name|optsArr
index|[
name|optsArr
operator|.
name|length
operator|-
literal|2
index|]
operator|=
name|src
expr_stmt|;
name|optsArr
index|[
name|optsArr
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|dst
expr_stmt|;
name|assertEquals
argument_list|(
name|exitCode
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|distCp
argument_list|,
name|optsArr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

