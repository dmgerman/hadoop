begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.fileContext
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|fileContext
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
name|FileContextMainOperationsBaseTest
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
name|s3a
operator|.
name|S3ATestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * S3A implementation of FileContextMainOperationsBaseTest.  */
end_comment

begin_class
DECL|class|ITestS3AFileContextMainOperations
specifier|public
class|class
name|ITestS3AFileContextMainOperations
extends|extends
name|FileContextMainOperationsBaseTest
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|fc
operator|=
name|S3ATestUtils
operator|.
name|createTestFileContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listCorruptedBlocksSupported ()
specifier|protected
name|boolean
name|listCorruptedBlocksSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testCreateFlagAppendExistingFile ()
specifier|public
name|void
name|testCreateFlagAppendExistingFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|//append not supported, so test removed
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testCreateFlagCreateAppendExistingFile ()
specifier|public
name|void
name|testCreateFlagCreateAppendExistingFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|//append not supported, so test removed
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testBuilderCreateAppendExistingFile ()
specifier|public
name|void
name|testBuilderCreateAppendExistingFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|// not supported
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testSetVerifyChecksum ()
specifier|public
name|void
name|testSetVerifyChecksum
parameter_list|()
throws|throws
name|IOException
block|{
comment|//checksums ignored, so test removed
block|}
block|}
end_class

end_unit

