begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|assertThat
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
comment|/**  * Test {@code PrintableString} class.  */
end_comment

begin_class
DECL|class|TestPrintableString
specifier|public
class|class
name|TestPrintableString
block|{
DECL|method|expect (String reason, String raw, String expected)
specifier|private
name|void
name|expect
parameter_list|(
name|String
name|reason
parameter_list|,
name|String
name|raw
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|reason
argument_list|,
operator|new
name|PrintableString
argument_list|(
name|raw
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test printable characters.    */
annotation|@
name|Test
DECL|method|testPrintableCharacters ()
specifier|public
name|void
name|testPrintableCharacters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// ASCII
name|expect
argument_list|(
literal|"Should keep ASCII letter"
argument_list|,
literal|"abcdef237"
argument_list|,
literal|"abcdef237"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should keep ASCII symbol"
argument_list|,
literal|" !\"|}~"
argument_list|,
literal|" !\"|}~"
argument_list|)
expr_stmt|;
comment|// Unicode BMP
name|expect
argument_list|(
literal|"Should keep Georgian U+1050 and Box Drawing U+2533"
argument_list|,
literal|"\u1050\u2533--"
argument_list|,
literal|"\u1050\u2533--"
argument_list|)
expr_stmt|;
comment|// Unicode SMP
name|expect
argument_list|(
literal|"Should keep Linear B U+10000 and Phoenician U+10900"
argument_list|,
literal|"\uD800\uDC00'''\uD802\uDD00"
argument_list|,
literal|"\uD800\uDC00'''\uD802\uDD00"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test non-printable characters.    */
annotation|@
name|Test
DECL|method|testNonPrintableCharacters ()
specifier|public
name|void
name|testNonPrintableCharacters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Control characters
name|expect
argument_list|(
literal|"Should replace single control character"
argument_list|,
literal|"abc\rdef"
argument_list|,
literal|"abc?def"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace multiple control characters"
argument_list|,
literal|"\babc\tdef"
argument_list|,
literal|"?abc?def"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace all control characters"
argument_list|,
literal|"\f\f\b\n"
argument_list|,
literal|"????"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace mixed characters starting with a control"
argument_list|,
literal|"\027ab\0"
argument_list|,
literal|"?ab?"
argument_list|)
expr_stmt|;
comment|// Formatting Unicode
name|expect
argument_list|(
literal|"Should replace Byte Order Mark"
argument_list|,
literal|"-\uFEFF--"
argument_list|,
literal|"-?--"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace Invisible Separator"
argument_list|,
literal|"\u2063\t"
argument_list|,
literal|"??"
argument_list|)
expr_stmt|;
comment|// Private use Unicode
name|expect
argument_list|(
literal|"Should replace private use U+E000"
argument_list|,
literal|"\uE000"
argument_list|,
literal|"?"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace private use U+E123 and U+F432"
argument_list|,
literal|"\uE123abc\uF432"
argument_list|,
literal|"?abc?"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace private use in Plane 15 and 16: U+F0000 and "
operator|+
literal|"U+10FFFD, but keep U+1050"
argument_list|,
literal|"x\uDB80\uDC00y\uDBFF\uDFFDz\u1050"
argument_list|,
literal|"x?y?z\u1050"
argument_list|)
expr_stmt|;
comment|// Unassigned Unicode
name|expect
argument_list|(
literal|"Should replace unassigned U+30000 and U+DFFFF"
argument_list|,
literal|"-\uD880\uDC00-\uDB3F\uDFFF-"
argument_list|,
literal|"-?-?-"
argument_list|)
expr_stmt|;
comment|// Standalone surrogate character (not in a pair)
name|expect
argument_list|(
literal|"Should replace standalone surrogate U+DB80"
argument_list|,
literal|"x\uDB80yz"
argument_list|,
literal|"x?yz"
argument_list|)
expr_stmt|;
name|expect
argument_list|(
literal|"Should replace standalone surrogate mixed with valid pair"
argument_list|,
literal|"x\uDB80\uD802\uDD00yz"
argument_list|,
literal|"x?\uD802\uDD00yz"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

