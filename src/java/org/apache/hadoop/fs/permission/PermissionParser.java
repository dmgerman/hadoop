begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
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
name|Matcher
import|;
end_import

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
comment|/**  * Base class for parsing either chmod permissions or umask permissions.  * Includes common code needed by either operation as implemented in  * UmaskParser and ChmodParser classes.  */
end_comment

begin_class
DECL|class|PermissionParser
class|class
name|PermissionParser
block|{
DECL|field|userMode
specifier|protected
name|short
name|userMode
decl_stmt|;
DECL|field|groupMode
specifier|protected
name|short
name|groupMode
decl_stmt|;
DECL|field|othersMode
specifier|protected
name|short
name|othersMode
decl_stmt|;
DECL|field|stickyMode
specifier|protected
name|short
name|stickyMode
decl_stmt|;
DECL|field|userType
specifier|protected
name|char
name|userType
init|=
literal|'+'
decl_stmt|;
DECL|field|groupType
specifier|protected
name|char
name|groupType
init|=
literal|'+'
decl_stmt|;
DECL|field|othersType
specifier|protected
name|char
name|othersType
init|=
literal|'+'
decl_stmt|;
DECL|field|stickyBitType
specifier|protected
name|char
name|stickyBitType
init|=
literal|'+'
decl_stmt|;
comment|/**    * Begin parsing permission stored in modeStr    *     * @param modeStr Permission mode, either octal or symbolic    * @param symbolic Use-case specific symbolic pattern to match against    * @throws IllegalArgumentException if unable to parse modeStr    */
DECL|method|PermissionParser (String modeStr, Pattern symbolic, Pattern octal)
specifier|public
name|PermissionParser
parameter_list|(
name|String
name|modeStr
parameter_list|,
name|Pattern
name|symbolic
parameter_list|,
name|Pattern
name|octal
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Matcher
name|matcher
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|matcher
operator|=
name|symbolic
operator|.
name|matcher
argument_list|(
name|modeStr
argument_list|)
operator|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|applyNormalPattern
argument_list|(
name|modeStr
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|matcher
operator|=
name|octal
operator|.
name|matcher
argument_list|(
name|modeStr
argument_list|)
operator|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|applyOctalPattern
argument_list|(
name|modeStr
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|modeStr
argument_list|)
throw|;
block|}
block|}
DECL|method|applyNormalPattern (String modeStr, Matcher matcher)
specifier|private
name|void
name|applyNormalPattern
parameter_list|(
name|String
name|modeStr
parameter_list|,
name|Matcher
name|matcher
parameter_list|)
block|{
comment|// Are there multiple permissions stored in one chmod?
name|boolean
name|commaSeperated
init|=
literal|false
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
literal|1
operator|||
name|matcher
operator|.
name|end
argument_list|()
operator|<
name|modeStr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
operator|!
name|commaSeperated
operator|||
operator|!
name|matcher
operator|.
name|find
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|modeStr
argument_list|)
throw|;
block|}
comment|/*        * groups : 1 : [ugoa]* 2 : [+-=] 3 : [rwxXt]+ 4 : [,\s]*        */
name|String
name|str
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|char
name|type
init|=
name|str
operator|.
name|charAt
argument_list|(
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|user
decl_stmt|,
name|group
decl_stmt|,
name|others
decl_stmt|,
name|stickyBit
decl_stmt|;
name|user
operator|=
name|group
operator|=
name|others
operator|=
name|stickyBit
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|char
name|c
range|:
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|toCharArray
argument_list|()
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'u'
case|:
name|user
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|'g'
case|:
name|group
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|'o'
case|:
name|others
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|'a'
case|:
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|user
operator|||
name|group
operator|||
name|others
operator|)
condition|)
block|{
comment|// same as specifying 'a'
name|user
operator|=
name|group
operator|=
name|others
operator|=
literal|true
expr_stmt|;
block|}
name|short
name|mode
init|=
literal|0
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
operator|.
name|toCharArray
argument_list|()
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'r'
case|:
name|mode
operator||=
literal|4
expr_stmt|;
break|break;
case|case
literal|'w'
case|:
name|mode
operator||=
literal|2
expr_stmt|;
break|break;
case|case
literal|'x'
case|:
name|mode
operator||=
literal|1
expr_stmt|;
break|break;
case|case
literal|'X'
case|:
name|mode
operator||=
literal|8
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|stickyBit
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|user
condition|)
block|{
name|userMode
operator|=
name|mode
expr_stmt|;
name|userType
operator|=
name|type
expr_stmt|;
block|}
if|if
condition|(
name|group
condition|)
block|{
name|groupMode
operator|=
name|mode
expr_stmt|;
name|groupType
operator|=
name|type
expr_stmt|;
block|}
if|if
condition|(
name|others
condition|)
block|{
name|othersMode
operator|=
name|mode
expr_stmt|;
name|othersType
operator|=
name|type
expr_stmt|;
name|stickyMode
operator|=
call|(
name|short
call|)
argument_list|(
name|stickyBit
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|stickyBitType
operator|=
name|type
expr_stmt|;
block|}
name|commaSeperated
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|.
name|contains
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|applyOctalPattern (String modeStr, Matcher matcher)
specifier|private
name|void
name|applyOctalPattern
parameter_list|(
name|String
name|modeStr
parameter_list|,
name|Matcher
name|matcher
parameter_list|)
block|{
name|userType
operator|=
name|groupType
operator|=
name|othersType
operator|=
literal|'='
expr_stmt|;
comment|// Check if sticky bit is specified
name|String
name|sb
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sb
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stickyMode
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|stickyBitType
operator|=
literal|'='
expr_stmt|;
block|}
name|String
name|str
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|userMode
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|groupMode
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|othersMode
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|combineModes (int existing, boolean exeOk)
specifier|protected
name|int
name|combineModes
parameter_list|(
name|int
name|existing
parameter_list|,
name|boolean
name|exeOk
parameter_list|)
block|{
return|return
name|combineModeSegments
argument_list|(
name|stickyBitType
argument_list|,
name|stickyMode
argument_list|,
operator|(
name|existing
operator|>>>
literal|9
operator|)
argument_list|,
literal|false
argument_list|)
operator|<<
literal|9
operator||
name|combineModeSegments
argument_list|(
name|userType
argument_list|,
name|userMode
argument_list|,
operator|(
name|existing
operator|>>>
literal|6
operator|)
operator|&
literal|7
argument_list|,
name|exeOk
argument_list|)
operator|<<
literal|6
operator||
name|combineModeSegments
argument_list|(
name|groupType
argument_list|,
name|groupMode
argument_list|,
operator|(
name|existing
operator|>>>
literal|3
operator|)
operator|&
literal|7
argument_list|,
name|exeOk
argument_list|)
operator|<<
literal|3
operator||
name|combineModeSegments
argument_list|(
name|othersType
argument_list|,
name|othersMode
argument_list|,
name|existing
operator|&
literal|7
argument_list|,
name|exeOk
argument_list|)
return|;
block|}
DECL|method|combineModeSegments (char type, int mode, int existing, boolean exeOk)
specifier|protected
name|int
name|combineModeSegments
parameter_list|(
name|char
name|type
parameter_list|,
name|int
name|mode
parameter_list|,
name|int
name|existing
parameter_list|,
name|boolean
name|exeOk
parameter_list|)
block|{
name|boolean
name|capX
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|(
name|mode
operator|&
literal|8
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// convert X to x;
name|capX
operator|=
literal|true
expr_stmt|;
name|mode
operator|&=
operator|~
literal|8
expr_stmt|;
name|mode
operator||=
literal|1
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|'+'
case|:
name|mode
operator|=
name|mode
operator||
name|existing
expr_stmt|;
break|break;
case|case
literal|'-'
case|:
name|mode
operator|=
operator|(
operator|~
name|mode
operator|)
operator|&
name|existing
expr_stmt|;
break|break;
case|case
literal|'='
case|:
break|break;
default|default  :
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected"
argument_list|)
throw|;
block|}
comment|// if X is specified add 'x' only if exeOk or x was already set.
if|if
condition|(
name|capX
operator|&&
operator|!
name|exeOk
operator|&&
operator|(
name|mode
operator|&
literal|1
operator|)
operator|!=
literal|0
operator|&&
operator|(
name|existing
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|mode
operator|&=
operator|~
literal|1
expr_stmt|;
comment|// remove x
block|}
return|return
name|mode
return|;
block|}
block|}
end_class

end_unit

