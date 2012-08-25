begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FromCpp
specifier|public
class|class
name|FromCpp
extends|extends
name|TestCase
block|{
DECL|method|FromCpp (String testName)
specifier|public
name|FromCpp
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{   }
DECL|method|testBinary ()
specifier|public
name|void
name|testBinary
parameter_list|()
block|{
name|File
name|tmpfile
decl_stmt|;
try|try
block|{
name|tmpfile
operator|=
operator|new
name|File
argument_list|(
literal|"/temp/hadooptmp.dat"
argument_list|)
expr_stmt|;
name|RecRecord1
name|r1
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r1
operator|.
name|setBoolVal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setByteVal
argument_list|(
operator|(
name|byte
operator|)
literal|0x66
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setFloatVal
argument_list|(
literal|3.145F
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setDoubleVal
argument_list|(
literal|1.5234
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setIntVal
argument_list|(
literal|4567
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setLongVal
argument_list|(
literal|0x5a5a5a5a5a5aL
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setStringVal
argument_list|(
literal|"random text"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setBufferVal
argument_list|(
operator|new
name|Buffer
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setVectorVal
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setMapVal
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|FileInputStream
name|istream
init|=
operator|new
name|FileInputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|BinaryRecordInput
name|in
init|=
operator|new
name|BinaryRecordInput
argument_list|(
name|istream
argument_list|)
decl_stmt|;
name|RecRecord1
name|r2
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r2
operator|.
name|deserialize
argument_list|(
name|in
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|equals
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCsv ()
specifier|public
name|void
name|testCsv
parameter_list|()
block|{
name|File
name|tmpfile
decl_stmt|;
try|try
block|{
name|tmpfile
operator|=
operator|new
name|File
argument_list|(
literal|"/temp/hadooptmp.txt"
argument_list|)
expr_stmt|;
name|RecRecord1
name|r1
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r1
operator|.
name|setBoolVal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setByteVal
argument_list|(
operator|(
name|byte
operator|)
literal|0x66
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setFloatVal
argument_list|(
literal|3.145F
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setDoubleVal
argument_list|(
literal|1.5234
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setIntVal
argument_list|(
literal|4567
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setLongVal
argument_list|(
literal|0x5a5a5a5a5a5aL
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setStringVal
argument_list|(
literal|"random text"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setBufferVal
argument_list|(
operator|new
name|Buffer
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setVectorVal
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setMapVal
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|FileInputStream
name|istream
init|=
operator|new
name|FileInputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|CsvRecordInput
name|in
init|=
operator|new
name|CsvRecordInput
argument_list|(
name|istream
argument_list|)
decl_stmt|;
name|RecRecord1
name|r2
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r2
operator|.
name|deserialize
argument_list|(
name|in
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|equals
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testXml ()
specifier|public
name|void
name|testXml
parameter_list|()
block|{
name|File
name|tmpfile
decl_stmt|;
try|try
block|{
name|tmpfile
operator|=
operator|new
name|File
argument_list|(
literal|"/temp/hadooptmp.xml"
argument_list|)
expr_stmt|;
name|RecRecord1
name|r1
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r1
operator|.
name|setBoolVal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setByteVal
argument_list|(
operator|(
name|byte
operator|)
literal|0x66
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setFloatVal
argument_list|(
literal|3.145F
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setDoubleVal
argument_list|(
literal|1.5234
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setIntVal
argument_list|(
literal|4567
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setLongVal
argument_list|(
literal|0x5a5a5a5a5a5aL
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setStringVal
argument_list|(
literal|"random text"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setBufferVal
argument_list|(
operator|new
name|Buffer
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setVectorVal
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|setMapVal
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|FileInputStream
name|istream
init|=
operator|new
name|FileInputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|XmlRecordInput
name|in
init|=
operator|new
name|XmlRecordInput
argument_list|(
name|istream
argument_list|)
decl_stmt|;
name|RecRecord1
name|r2
init|=
operator|new
name|RecRecord1
argument_list|()
decl_stmt|;
name|r2
operator|.
name|deserialize
argument_list|(
name|in
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|equals
argument_list|(
name|r2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

