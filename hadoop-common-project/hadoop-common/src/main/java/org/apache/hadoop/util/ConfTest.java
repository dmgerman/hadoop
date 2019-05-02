begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|FileFilter
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
name|io
operator|.
name|InputStream
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
name|Files
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|List
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
name|Stack
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
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLEventReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|events
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|events
operator|.
name|Characters
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|events
operator|.
name|StartElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|events
operator|.
name|XMLEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLineParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|GnuParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|MissingArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|OptionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
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

begin_comment
comment|/**  * This class validates configuration XML files in ${HADOOP_CONF_DIR} or  * specified ones.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ConfTest
specifier|public
specifier|final
class|class
name|ConfTest
block|{
DECL|field|USAGE
specifier|private
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"Usage: hadoop conftest [-conffile<path>|-h|--help]\n"
operator|+
literal|"  Options:\n"
operator|+
literal|"  \n"
operator|+
literal|"  -conffile<path>\n"
operator|+
literal|"    If not specified, the files in ${HADOOP_CONF_DIR}\n"
operator|+
literal|"    whose name end with .xml will be verified.\n"
operator|+
literal|"    If specified, that path will be verified.\n"
operator|+
literal|"    You can specify either a file or directory, and\n"
operator|+
literal|"    if a directory specified, the files in that directory\n"
operator|+
literal|"    whose name end with .xml will be verified.\n"
operator|+
literal|"    You can specify this option multiple times.\n"
operator|+
literal|"  -h, --help       Print this help"
decl_stmt|;
DECL|field|HADOOP_CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP_CONF_DIR
init|=
literal|"HADOOP_CONF_DIR"
decl_stmt|;
DECL|method|ConfTest ()
specifier|protected
name|ConfTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|parseConf (InputStream in)
specifier|private
specifier|static
name|List
argument_list|<
name|NodeInfo
argument_list|>
name|parseConf
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|QName
name|configuration
init|=
operator|new
name|QName
argument_list|(
literal|"configuration"
argument_list|)
decl_stmt|;
name|QName
name|property
init|=
operator|new
name|QName
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeInfo
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Stack
argument_list|<
name|NodeInfo
argument_list|>
name|parsed
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|XMLEventReader
name|reader
init|=
name|factory
operator|.
name|createXMLEventReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|XMLEvent
name|event
init|=
name|reader
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|isStartElement
argument_list|()
condition|)
block|{
name|StartElement
name|currentElement
init|=
name|event
operator|.
name|asStartElement
argument_list|()
decl_stmt|;
name|NodeInfo
name|currentNode
init|=
operator|new
name|NodeInfo
argument_list|(
name|currentElement
argument_list|)
decl_stmt|;
if|if
condition|(
name|parsed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|currentElement
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|configuration
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
name|NodeInfo
name|parentNode
init|=
name|parsed
operator|.
name|peek
argument_list|()
decl_stmt|;
name|QName
name|parentName
init|=
name|parentNode
operator|.
name|getStartElement
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentName
operator|.
name|equals
argument_list|(
name|configuration
argument_list|)
operator|&&
name|currentNode
operator|.
name|getStartElement
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|property
argument_list|)
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Iterator
argument_list|<
name|Attribute
argument_list|>
name|it
init|=
name|currentElement
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentNode
operator|.
name|addAttribute
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|parentName
operator|.
name|equals
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|parentNode
operator|.
name|addElement
argument_list|(
name|currentElement
argument_list|)
expr_stmt|;
block|}
block|}
name|parsed
operator|.
name|push
argument_list|(
name|currentNode
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|isEndElement
argument_list|()
condition|)
block|{
name|NodeInfo
name|node
init|=
name|parsed
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|parsed
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|isCharacters
argument_list|()
condition|)
block|{
if|if
condition|(
literal|2
operator|<
name|parsed
operator|.
name|size
argument_list|()
condition|)
block|{
name|NodeInfo
name|parentNode
init|=
name|parsed
operator|.
name|pop
argument_list|()
decl_stmt|;
name|StartElement
name|parentElement
init|=
name|parentNode
operator|.
name|getStartElement
argument_list|()
decl_stmt|;
name|NodeInfo
name|grandparentNode
init|=
name|parsed
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|grandparentNode
operator|.
name|getElement
argument_list|(
name|parentElement
argument_list|)
operator|==
literal|null
condition|)
block|{
name|grandparentNode
operator|.
name|setElement
argument_list|(
name|parentElement
argument_list|,
name|event
operator|.
name|asCharacters
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|parsed
operator|.
name|push
argument_list|(
name|parentNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nodes
return|;
block|}
DECL|method|checkConf (InputStream in)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|checkConf
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|List
argument_list|<
name|NodeInfo
argument_list|>
name|nodes
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|nodes
operator|=
name|parseConf
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
literal|"bad conf file: top-level element not<configuration>"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
literal|"bad conf file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|errors
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|duplicatedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeInfo
name|node
range|:
name|nodes
control|)
block|{
name|StartElement
name|element
init|=
name|node
operator|.
name|getStartElement
argument_list|()
decl_stmt|;
name|int
name|line
init|=
name|element
operator|.
name|getLocation
argument_list|()
operator|.
name|getLineNumber
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|element
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|QName
argument_list|(
literal|"property"
argument_list|)
argument_list|)
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %d: element not<property>"
argument_list|,
name|line
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|List
argument_list|<
name|XMLEvent
argument_list|>
name|events
init|=
name|node
operator|.
name|getXMLEventsForQName
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|events
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %d:<property> has no<name>"
argument_list|,
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|v
init|=
literal|null
decl_stmt|;
for|for
control|(
name|XMLEvent
name|event
range|:
name|events
control|)
block|{
if|if
condition|(
name|event
operator|.
name|isAttribute
argument_list|()
condition|)
block|{
name|v
operator|=
operator|(
operator|(
name|Attribute
operator|)
name|event
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Characters
name|c
init|=
name|node
operator|.
name|getElement
argument_list|(
name|event
operator|.
name|asStartElement
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|c
operator|.
name|getData
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|v
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %d:<property> has an empty<name>"
argument_list|,
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
operator|!
name|v
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|lines
init|=
name|duplicatedProperties
operator|.
name|get
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|lines
operator|==
literal|null
condition|)
block|{
name|lines
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|duplicatedProperties
operator|.
name|put
argument_list|(
name|v
argument_list|,
name|lines
argument_list|)
expr_stmt|;
block|}
name|lines
operator|.
name|add
argument_list|(
name|node
operator|.
name|getStartElement
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|events
operator|=
name|node
operator|.
name|getXMLEventsForQName
argument_list|(
operator|new
name|QName
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|events
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %d:<property> has no<value>"
argument_list|,
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QName
name|qName
range|:
name|node
operator|.
name|getDuplicatedQNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|qName
operator|.
name|equals
argument_list|(
operator|new
name|QName
argument_list|(
literal|"source"
argument_list|)
argument_list|)
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %d:<property> has duplicated<%s>s"
argument_list|,
name|line
argument_list|,
name|qName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|e
range|:
name|duplicatedProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|lines
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|1
operator|<
name|lines
operator|.
name|size
argument_list|()
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Line %s: duplicated<property>s for %s"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|lines
argument_list|)
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|errors
return|;
block|}
DECL|method|listFiles (File dir)
specifier|private
specifier|static
name|File
index|[]
name|listFiles
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
return|return
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|isFile
argument_list|()
operator|&&
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
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
name|GenericOptionsParser
name|genericParser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|String
index|[]
name|remainingArgs
init|=
name|genericParser
operator|.
name|getRemainingArgs
argument_list|()
decl_stmt|;
name|Option
name|conf
init|=
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
operator|.
name|create
argument_list|(
literal|"conffile"
argument_list|)
decl_stmt|;
name|Option
name|help
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"help"
argument_list|)
operator|.
name|create
argument_list|(
literal|'h'
argument_list|)
decl_stmt|;
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
operator|.
name|addOption
argument_list|(
name|conf
argument_list|)
operator|.
name|addOption
argument_list|(
name|help
argument_list|)
decl_stmt|;
name|CommandLineParser
name|specificParser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|specificParser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|remainingArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingArgumentException
name|e
parameter_list|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
literal|"No argument specified for -conffile option"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
literal|"Failed to parse options"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|'h'
argument_list|)
condition|)
block|{
name|terminate
argument_list|(
literal|0
argument_list|,
name|USAGE
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|File
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"conffile"
argument_list|)
condition|)
block|{
name|String
index|[]
name|values
init|=
name|cmd
operator|.
name|getOptionValues
argument_list|(
literal|"conffile"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|File
name|confFile
init|=
operator|new
name|File
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|confFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|confFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|confFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|files
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|listFiles
argument_list|(
name|confFile
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
name|confFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" is neither a file nor directory"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
name|confDirName
init|=
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_CONF_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|confDirName
operator|==
literal|null
condition|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
name|HADOOP_CONF_DIR
operator|+
literal|" is not defined"
argument_list|)
expr_stmt|;
block|}
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|confDirName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|confDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
name|HADOOP_CONF_DIR
operator|+
literal|" is not a directory"
argument_list|)
expr_stmt|;
block|}
name|files
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|listFiles
argument_list|(
name|confDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|files
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
literal|"No input file to validate"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|String
name|path
init|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
name|checkConf
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|path
operator|+
literal|": valid"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|path
operator|+
literal|":"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|error
range|:
name|errors
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\t"
operator|+
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terminate
argument_list|(
literal|1
argument_list|,
literal|"Invalid file exists"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|terminate (int status, String msg)
specifier|private
specifier|static
name|void
name|terminate
parameter_list|(
name|int
name|status
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|NodeInfo
class|class
name|NodeInfo
block|{
DECL|field|startElement
specifier|private
name|StartElement
name|startElement
decl_stmt|;
DECL|field|attributes
specifier|private
name|List
argument_list|<
name|Attribute
argument_list|>
name|attributes
init|=
operator|new
name|ArrayList
argument_list|<
name|Attribute
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|elements
specifier|private
name|Map
argument_list|<
name|StartElement
argument_list|,
name|Characters
argument_list|>
name|elements
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|qNameXMLEventsMap
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|List
argument_list|<
name|XMLEvent
argument_list|>
argument_list|>
name|qNameXMLEventsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|NodeInfo (StartElement startElement)
specifier|public
name|NodeInfo
parameter_list|(
name|StartElement
name|startElement
parameter_list|)
block|{
name|this
operator|.
name|startElement
operator|=
name|startElement
expr_stmt|;
block|}
DECL|method|addQNameXMLEvent (QName qName, XMLEvent event)
specifier|private
name|void
name|addQNameXMLEvent
parameter_list|(
name|QName
name|qName
parameter_list|,
name|XMLEvent
name|event
parameter_list|)
block|{
name|List
argument_list|<
name|XMLEvent
argument_list|>
name|events
init|=
name|qNameXMLEventsMap
operator|.
name|get
argument_list|(
name|qName
argument_list|)
decl_stmt|;
if|if
condition|(
name|events
operator|==
literal|null
condition|)
block|{
name|events
operator|=
operator|new
name|ArrayList
argument_list|<
name|XMLEvent
argument_list|>
argument_list|()
expr_stmt|;
name|qNameXMLEventsMap
operator|.
name|put
argument_list|(
name|qName
argument_list|,
name|events
argument_list|)
expr_stmt|;
block|}
name|events
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|getStartElement ()
specifier|public
name|StartElement
name|getStartElement
parameter_list|()
block|{
return|return
name|startElement
return|;
block|}
DECL|method|addAttribute (Attribute attribute)
specifier|public
name|void
name|addAttribute
parameter_list|(
name|Attribute
name|attribute
parameter_list|)
block|{
name|attributes
operator|.
name|add
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
name|addQNameXMLEvent
argument_list|(
name|attribute
operator|.
name|getName
argument_list|()
argument_list|,
name|attribute
argument_list|)
expr_stmt|;
block|}
DECL|method|getElement (StartElement element)
specifier|public
name|Characters
name|getElement
parameter_list|(
name|StartElement
name|element
parameter_list|)
block|{
return|return
name|elements
operator|.
name|get
argument_list|(
name|element
argument_list|)
return|;
block|}
DECL|method|addElement (StartElement element)
specifier|public
name|void
name|addElement
parameter_list|(
name|StartElement
name|element
parameter_list|)
block|{
name|setElement
argument_list|(
name|element
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|addQNameXMLEvent
argument_list|(
name|element
operator|.
name|getName
argument_list|()
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
DECL|method|setElement (StartElement element, Characters text)
specifier|public
name|void
name|setElement
parameter_list|(
name|StartElement
name|element
parameter_list|,
name|Characters
name|text
parameter_list|)
block|{
name|elements
operator|.
name|put
argument_list|(
name|element
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|getDuplicatedQNames ()
specifier|public
name|List
argument_list|<
name|QName
argument_list|>
name|getDuplicatedQNames
parameter_list|()
block|{
name|List
argument_list|<
name|QName
argument_list|>
name|duplicates
init|=
operator|new
name|ArrayList
argument_list|<
name|QName
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|QName
argument_list|,
name|List
argument_list|<
name|XMLEvent
argument_list|>
argument_list|>
name|e
range|:
name|qNameXMLEventsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
literal|1
operator|<
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|duplicates
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|duplicates
return|;
block|}
DECL|method|getXMLEventsForQName (QName qName)
specifier|public
name|List
argument_list|<
name|XMLEvent
argument_list|>
name|getXMLEventsForQName
parameter_list|(
name|QName
name|qName
parameter_list|)
block|{
return|return
name|qNameXMLEventsMap
operator|.
name|get
argument_list|(
name|qName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

