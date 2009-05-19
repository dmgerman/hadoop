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
name|InputStream
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_comment
comment|/**  * XML Deserializer.  */
end_comment

begin_class
DECL|class|XmlRecordInput
specifier|public
class|class
name|XmlRecordInput
implements|implements
name|RecordInput
block|{
DECL|class|Value
specifier|static
specifier|private
class|class
name|Value
block|{
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|sb
specifier|private
name|StringBuffer
name|sb
decl_stmt|;
DECL|method|Value (String t)
specifier|public
name|Value
parameter_list|(
name|String
name|t
parameter_list|)
block|{
name|type
operator|=
name|t
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
block|}
DECL|method|addChars (char[] buf, int offset, int len)
specifier|public
name|void
name|addChars
parameter_list|(
name|char
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
DECL|class|XMLParser
specifier|private
specifier|static
class|class
name|XMLParser
extends|extends
name|DefaultHandler
block|{
DECL|field|charsValid
specifier|private
name|boolean
name|charsValid
init|=
literal|false
decl_stmt|;
DECL|field|valList
specifier|private
name|ArrayList
argument_list|<
name|Value
argument_list|>
name|valList
decl_stmt|;
DECL|method|XMLParser (ArrayList<Value> vlist)
specifier|private
name|XMLParser
parameter_list|(
name|ArrayList
argument_list|<
name|Value
argument_list|>
name|vlist
parameter_list|)
block|{
name|valList
operator|=
name|vlist
expr_stmt|;
block|}
DECL|method|startDocument ()
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{}
DECL|method|endDocument ()
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{}
DECL|method|startElement (String ns, String sname, String qname, Attributes attrs)
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|ns
parameter_list|,
name|String
name|sname
parameter_list|,
name|String
name|qname
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|SAXException
block|{
name|charsValid
operator|=
literal|false
expr_stmt|;
if|if
condition|(
literal|"boolean"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"i4"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"int"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"string"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"double"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"ex:i1"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"ex:i8"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"ex:float"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
condition|)
block|{
name|charsValid
operator|=
literal|true
expr_stmt|;
name|valList
operator|.
name|add
argument_list|(
operator|new
name|Value
argument_list|(
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"struct"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"array"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
condition|)
block|{
name|valList
operator|.
name|add
argument_list|(
operator|new
name|Value
argument_list|(
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|endElement (String ns, String sname, String qname)
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|ns
parameter_list|,
name|String
name|sname
parameter_list|,
name|String
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
name|charsValid
operator|=
literal|false
expr_stmt|;
if|if
condition|(
literal|"struct"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
operator|||
literal|"array"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
condition|)
block|{
name|valList
operator|.
name|add
argument_list|(
operator|new
name|Value
argument_list|(
literal|"/"
operator|+
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|characters (char buf[], int offset, int len)
specifier|public
name|void
name|characters
parameter_list|(
name|char
name|buf
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|charsValid
condition|)
block|{
name|Value
name|v
init|=
name|valList
operator|.
name|get
argument_list|(
name|valList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|v
operator|.
name|addChars
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|XmlIndex
specifier|private
class|class
name|XmlIndex
implements|implements
name|Index
block|{
DECL|method|done ()
specifier|public
name|boolean
name|done
parameter_list|()
block|{
name|Value
name|v
init|=
name|valList
operator|.
name|get
argument_list|(
name|vIdx
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"/array"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|valList
operator|.
name|set
argument_list|(
name|vIdx
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|vIdx
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|incr ()
specifier|public
name|void
name|incr
parameter_list|()
block|{}
block|}
DECL|field|valList
specifier|private
name|ArrayList
argument_list|<
name|Value
argument_list|>
name|valList
decl_stmt|;
DECL|field|vLen
specifier|private
name|int
name|vLen
decl_stmt|;
DECL|field|vIdx
specifier|private
name|int
name|vIdx
decl_stmt|;
DECL|method|next ()
specifier|private
name|Value
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|vIdx
operator|<
name|vLen
condition|)
block|{
name|Value
name|v
init|=
name|valList
operator|.
name|get
argument_list|(
name|vIdx
argument_list|)
decl_stmt|;
name|valList
operator|.
name|set
argument_list|(
name|vIdx
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|vIdx
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error in deserialization."
argument_list|)
throw|;
block|}
block|}
comment|/** Creates a new instance of XmlRecordInput */
DECL|method|XmlRecordInput (InputStream in)
specifier|public
name|XmlRecordInput
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
try|try
block|{
name|valList
operator|=
operator|new
name|ArrayList
argument_list|<
name|Value
argument_list|>
argument_list|()
expr_stmt|;
name|DefaultHandler
name|handler
init|=
operator|new
name|XMLParser
argument_list|(
name|valList
argument_list|)
decl_stmt|;
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|vLen
operator|=
name|valList
operator|.
name|size
argument_list|()
expr_stmt|;
name|vIdx
operator|=
literal|0
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|readByte (String tag)
specifier|public
name|byte
name|readByte
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"ex:i1"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readBool (String tag)
specifier|public
name|boolean
name|readBool
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"boolean"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
literal|"1"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readInt (String tag)
specifier|public
name|int
name|readInt
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"i4"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
operator|!
literal|"int"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readLong (String tag)
specifier|public
name|long
name|readLong
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"ex:i8"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readFloat (String tag)
specifier|public
name|float
name|readFloat
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"ex:float"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readDouble (String tag)
specifier|public
name|double
name|readDouble
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"double"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readString (String tag)
specifier|public
name|String
name|readString
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"string"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Utils
operator|.
name|fromXMLString
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readBuffer (String tag)
specifier|public
name|Buffer
name|readBuffer
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"string"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|Utils
operator|.
name|fromXMLBuffer
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|startRecord (String tag)
specifier|public
name|void
name|startRecord
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"struct"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
block|}
DECL|method|endRecord (String tag)
specifier|public
name|void
name|endRecord
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"/struct"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
block|}
DECL|method|startVector (String tag)
specifier|public
name|Index
name|startVector
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Value
name|v
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"array"
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error deserializing "
operator|+
name|tag
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
operator|new
name|XmlIndex
argument_list|()
return|;
block|}
DECL|method|endVector (String tag)
specifier|public
name|void
name|endVector
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|startMap (String tag)
specifier|public
name|Index
name|startMap
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|startVector
argument_list|(
name|tag
argument_list|)
return|;
block|}
DECL|method|endMap (String tag)
specifier|public
name|void
name|endMap
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|endVector
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

