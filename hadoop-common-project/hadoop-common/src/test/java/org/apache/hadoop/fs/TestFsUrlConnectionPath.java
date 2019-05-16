begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_comment
comment|/**  * Test case for FsUrlConnection with relativePath and SPACE.  */
end_comment

begin_class
DECL|class|TestFsUrlConnectionPath
specifier|public
class|class
name|TestFsUrlConnectionPath
block|{
DECL|field|CURRENT
specifier|private
specifier|static
specifier|final
name|String
name|CURRENT
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|""
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|ABSOLUTE_PATH
specifier|private
specifier|static
specifier|final
name|String
name|ABSOLUTE_PATH
init|=
literal|"file:"
operator|+
name|CURRENT
operator|+
literal|"/abs.txt"
decl_stmt|;
DECL|field|RELATIVE_PATH
specifier|private
specifier|static
specifier|final
name|String
name|RELATIVE_PATH
init|=
literal|"file:relative.txt"
decl_stmt|;
DECL|field|ABSOLUTE_PATH_W_SPACE
specifier|private
specifier|static
specifier|final
name|String
name|ABSOLUTE_PATH_W_SPACE
init|=
literal|"file:"
operator|+
name|CURRENT
operator|+
literal|"/abs 1.txt"
decl_stmt|;
DECL|field|RELATIVE_PATH_W_SPACE
specifier|private
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_W_SPACE
init|=
literal|"file:relative 1.txt"
decl_stmt|;
DECL|field|ABSOLUTE_PATH_W_ENCODED_SPACE
specifier|private
specifier|static
specifier|final
name|String
name|ABSOLUTE_PATH_W_ENCODED_SPACE
init|=
literal|"file:"
operator|+
name|CURRENT
operator|+
literal|"/abs%201.txt"
decl_stmt|;
DECL|field|RELATIVE_PATH_W_ENCODED_SPACE
specifier|private
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_W_ENCODED_SPACE
init|=
literal|"file:relative%201.txt"
decl_stmt|;
DECL|field|DATA
specifier|private
specifier|static
specifier|final
name|String
name|DATA
init|=
literal|"data"
decl_stmt|;
DECL|field|CONFIGURATION
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONFIGURATION
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initialize ()
specifier|public
specifier|static
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|ABSOLUTE_PATH
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|,
name|DATA
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|RELATIVE_PATH
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|,
name|DATA
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|ABSOLUTE_PATH_W_SPACE
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|,
name|DATA
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|RELATIVE_PATH_W_SPACE
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|,
name|DATA
argument_list|)
expr_stmt|;
name|URL
operator|.
name|setURLStreamHandlerFactory
argument_list|(
operator|new
name|FsUrlStreamHandlerFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|delete
argument_list|(
name|ABSOLUTE_PATH
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|RELATIVE_PATH
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|ABSOLUTE_PATH_W_SPACE
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|RELATIVE_PATH_W_SPACE
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (String path)
specifier|public
specifier|static
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|write (String path, String data)
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|fw
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readStream (String path)
specifier|public
specifier|static
name|int
name|readStream
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
return|return
name|is
operator|.
name|available
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testAbsolutePath ()
specifier|public
name|void
name|testAbsolutePath
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|readStream
argument_list|(
name|ABSOLUTE_PATH
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRelativePath ()
specifier|public
name|void
name|testRelativePath
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|readStream
argument_list|(
name|RELATIVE_PATH
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbsolutePathWithSpace ()
specifier|public
name|void
name|testAbsolutePathWithSpace
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|readStream
argument_list|(
name|ABSOLUTE_PATH_W_ENCODED_SPACE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRelativePathWithSpace ()
specifier|public
name|void
name|testRelativePathWithSpace
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|readStream
argument_list|(
name|RELATIVE_PATH_W_ENCODED_SPACE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

