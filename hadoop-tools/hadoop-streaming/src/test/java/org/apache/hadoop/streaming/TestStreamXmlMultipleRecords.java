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
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests if StreamXmlRecordReader will read the next record, _after_ the  * end of a split if the split falls before the end of end-tag of a record.  * Also tests if StreamXmlRecordReader will read a record twice if end of a  * split is after few characters after the end-tag of a record but before the  * begin-tag of next record.  */
end_comment

begin_class
DECL|class|TestStreamXmlMultipleRecords
specifier|public
class|class
name|TestStreamXmlMultipleRecords
extends|extends
name|TestStreaming
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestStreamXmlMultipleRecords
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hasPerl
specifier|private
name|boolean
name|hasPerl
init|=
literal|false
decl_stmt|;
DECL|field|blockSize
specifier|private
name|long
name|blockSize
decl_stmt|;
DECL|field|isSlowMatch
specifier|private
name|String
name|isSlowMatch
decl_stmt|;
comment|// Our own configuration used for creating FileSystem object where
comment|// fs.local.block.size is set to 60 OR 80.
comment|// See 60th char in input. It is before the end of end-tag of a record.
comment|// See 80th char in input. It is in between the end-tag of a record and
comment|// the begin-tag of next record.
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|myPerlMapper
specifier|private
name|String
name|myPerlMapper
init|=
literal|"perl -n -a -e 'print join(qq(\\n), map { qq($_\\t1) } @F), qq(\\n);'"
decl_stmt|;
DECL|field|myPerlReducer
specifier|private
name|String
name|myPerlReducer
init|=
literal|"perl -n -a -e '$freq{$F[0]}++; END { print qq(is\\t$freq{is}\\n); }'"
decl_stmt|;
DECL|method|TestStreamXmlMultipleRecords ()
specifier|public
name|TestStreamXmlMultipleRecords
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|input
operator|=
literal|"<line>This is a single line,\nand it is containing multiple"
operator|+
literal|" words.</line><line>Only is appears more than"
operator|+
literal|" once.</line>\n"
expr_stmt|;
name|outputExpect
operator|=
literal|"is\t3\n"
expr_stmt|;
name|map
operator|=
name|myPerlMapper
expr_stmt|;
name|reduce
operator|=
name|myPerlReducer
expr_stmt|;
name|hasPerl
operator|=
name|UtilTest
operator|.
name|hasPerlSupport
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// Without this closeAll() call, setting of FileSystem block size is
comment|// not effective and will be old block size set in earlier test.
name|FileSystem
operator|.
name|closeAll
argument_list|()
expr_stmt|;
block|}
comment|// Set file system block size such that split falls
comment|// (a) before the end of end-tag of a record (testStreamXmlMultiInner...) OR
comment|// (b) between records(testStreamXmlMultiOuter...)
annotation|@
name|Override
DECL|method|getConf ()
specifier|protected
name|Configuration
name|getConf
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
literal|"fs.local.block.size"
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|genArgs ()
specifier|protected
name|String
index|[]
name|genArgs
parameter_list|()
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-inputreader"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"StreamXmlRecordReader,begin=<line>,end=</line>,slowmatch="
operator|+
name|isSlowMatch
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|genArgs
argument_list|()
return|;
block|}
comment|/**    * Tests if StreamXmlRecordReader will read the next record, _after_ the    * end of a split if the split falls before the end of end-tag of a record.    * Tests with slowmatch=false.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testStreamXmlMultiInnerFast ()
specifier|public
name|void
name|testStreamXmlMultiInnerFast
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hasPerl
condition|)
block|{
name|blockSize
operator|=
literal|60
expr_stmt|;
name|isSlowMatch
operator|=
literal|"false"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No perl; skipping test."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests if StreamXmlRecordReader will read a record twice if end of a    * split is after few characters after the end-tag of a record but before the    * begin-tag of next record.    * Tests with slowmatch=false.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testStreamXmlMultiOuterFast ()
specifier|public
name|void
name|testStreamXmlMultiOuterFast
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hasPerl
condition|)
block|{
name|blockSize
operator|=
literal|80
expr_stmt|;
name|isSlowMatch
operator|=
literal|"false"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No perl; skipping test."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests if StreamXmlRecordReader will read the next record, _after_ the    * end of a split if the split falls before the end of end-tag of a record.    * Tests with slowmatch=true.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testStreamXmlMultiInnerSlow ()
specifier|public
name|void
name|testStreamXmlMultiInnerSlow
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hasPerl
condition|)
block|{
name|blockSize
operator|=
literal|60
expr_stmt|;
name|isSlowMatch
operator|=
literal|"true"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No perl; skipping test."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests if StreamXmlRecordReader will read a record twice if end of a    * split is after few characters after the end-tag of a record but before the    * begin-tag of next record.    * Tests with slowmatch=true.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testStreamXmlMultiOuterSlow ()
specifier|public
name|void
name|testStreamXmlMultiOuterSlow
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hasPerl
condition|)
block|{
name|blockSize
operator|=
literal|80
expr_stmt|;
name|isSlowMatch
operator|=
literal|"true"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No perl; skipping test."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testCommandLine ()
specifier|public
name|void
name|testCommandLine
parameter_list|()
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

